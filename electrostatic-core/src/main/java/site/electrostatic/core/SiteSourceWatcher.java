package site.electrostatic.core;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
final class SiteSourceWatcher implements AutoCloseable {

    private final Path inputDirectory;
    private final Path outputDirectory;
    private final WatchService watchService;
    private final Map<WatchKey, Path> directories = new HashMap<>();
    private final Thread thread;

    SiteSourceWatcher(Path inputDirectory, Path outputDirectory, Runnable rebuild) throws IOException {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.watchService = inputDirectory.getFileSystem().newWatchService();
        try {
            registerDirectories(inputDirectory);
        } catch (IOException | RuntimeException e) {
            watchService.close();
            throw e;
        }
        thread = new Thread(() -> watch(rebuild), "electrostatic-watch");
        thread.setDaemon(true);
    }

    void start() {
        thread.start();
    }

    private boolean ignored(Path path) {
        if (path.startsWith(outputDirectory)) {
            return true;
        }
        for (Path segment : inputDirectory.relativize(path)) {
            if (segment.toString().equals(".git")) {
                return true;
            }
        }
        return false;
    }

    private void registerDirectories(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                if (ignored(dir)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                WatchKey key = dir.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
                directories.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private boolean process(WatchKey key) {
        Path directory = directories.get(key);
        boolean changed = false;
        for (var event : key.pollEvents()) {
            if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                changed = true;
                try {
                    registerDirectories(inputDirectory);
                } catch (IOException e) {
                    log.warn("Unable to refresh watched directories after event overflow", e);
                }
                continue;
            }
            if (directory == null) {
                continue;
            }
            Path path = directory.resolve((Path) event.context());
            if (ignored(path)) {
                continue;
            }
            changed = true;
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(path)) {
                try {
                    registerDirectories(path);
                } catch (IOException e) {
                    log.warn("Unable to watch new directory: {}", path, e);
                }
            }
        }
        if (!key.reset()) {
            directories.remove(key);
        }
        return changed;
    }

    private void watch(Runnable rebuild) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                boolean changed = process(watchService.take());
                WatchKey key;
                while ((key = watchService.poll(250, TimeUnit.MILLISECONDS)) != null) {
                    changed |= process(key);
                }
                if (changed && !Thread.currentThread().isInterrupted()) {
                    try {
                        rebuild.run();
                    } catch (Exception e) {
                        log.error("Unable to rebuild site; watching for further changes", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ClosedWatchServiceException ignored) {
            // Closing the preview session stops the watcher.
        }
    }

    @Override
    public void close() {
        try {
            watchService.close();
        } catch (IOException e) {
            log.warn("Unable to close site watcher", e);
        }
        thread.interrupt();
        if (Thread.currentThread() != thread) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
