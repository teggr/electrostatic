package io.github.teggr.electrostatic.core;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
final class SiteSourceWatcher implements AutoCloseable {

    private final Path inputDirectory;
    private final Path outputDirectory;
    private final Path repositoryRoot;
    private final WatchService watchService;
    private final Map<WatchKey, Path> directories = new HashMap<>();
    private final Map<Path, List<IgnoreRule>> ignoreRules = new HashMap<>();
    private final Thread thread;

    SiteSourceWatcher(Path inputDirectory, Path outputDirectory, Runnable rebuild) throws IOException {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.repositoryRoot = findRepositoryRoot(inputDirectory);
        this.watchService = inputDirectory.getFileSystem().newWatchService();
        try {
            registerIgnoreAncestorDirectories();
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
        return ignoredByGitignore(path);
    }

    private boolean ignoredByGitignore(Path path) {
        if (path.equals(inputDirectory)) {
            return false;
        }
        boolean ignored = false;
        Path ruleDirectory = path.getParent();
        List<Path> ruleDirectories = new ArrayList<>();
        while (ruleDirectory != null && ruleDirectory.startsWith(repositoryRoot)) {
            ruleDirectories.add(ruleDirectory);
            if (ruleDirectory.equals(repositoryRoot)) {
                break;
            }
            ruleDirectory = ruleDirectory.getParent();
        }
        for (int depth = ruleDirectories.size() - 1; depth >= 0; depth--) {
            ruleDirectory = ruleDirectories.get(depth);
            List<IgnoreRule> rules = ignoreRules.get(ruleDirectory);
            if (rules == null) {
                continue;
            }
            Path relativeToRules = ruleDirectory.relativize(path);
            for (IgnoreRule rule : rules) {
                if (rule.matches(ruleDirectory, relativeToRules, path)) {
                    ignored = !rule.negated();
                }
            }
        }
        return ignored;
    }

    private Path findRepositoryRoot(Path path) {
        Path directory = path;
        while (directory != null) {
            if (Files.exists(directory.resolve(".git"))) {
                return directory;
            }
            directory = directory.getParent();
        }
        return inputDirectory;
    }

    private void loadIgnoreRules(Path directory) {
        Path ignoreFile = directory.resolve(".gitignore");
        if (!Files.isRegularFile(ignoreFile)) {
            ignoreRules.remove(directory);
            return;
        }
        try {
            List<IgnoreRule> rules = new ArrayList<>();
            for (String line : Files.readAllLines(ignoreFile)) {
                IgnoreRule.parse(directory, line).ifPresent(rules::add);
            }
            ignoreRules.put(directory, rules);
        } catch (IOException e) {
            log.warn("Unable to read ignore rules from {}", ignoreFile, e);
            ignoreRules.remove(directory);
        }
    }

    private void refreshDirectories() throws IOException {
        directories.keySet().forEach(WatchKey::cancel);
        directories.clear();
        ignoreRules.clear();
        registerIgnoreAncestorDirectories();
        registerDirectories(inputDirectory);
    }

    private void registerIgnoreAncestorDirectories() throws IOException {
        Path directory = repositoryRoot;
        while (directory != null && !directory.equals(inputDirectory)) {
            WatchKey key = directory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
            directories.put(key, directory);
            loadIgnoreRules(directory);
            if (directory.equals(inputDirectory.getParent())) {
                break;
            }
            directory = directory.getParent();
        }
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
                loadIgnoreRules(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private boolean process(WatchKey key, Set<String> changedPaths) {
        Path directory = directories.get(key);
        boolean changed = false;
        for (var event : key.pollEvents()) {
            if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                changed = true;
                changedPaths.add("filesystem event overflow (some changed paths may be missing)");
                log.warn("Filesystem watcher event overflow; some changed paths may be missing");
                try {
                    refreshDirectories();
                } catch (IOException e) {
                    log.warn("Unable to refresh watched directories after event overflow", e);
                }
                continue;
            }
            if (directory == null) {
                continue;
            }
            Path path = directory.resolve((Path) event.context());
            if (path.getFileName().toString().equals(".gitignore")) {
                changed = true;
                changedPaths.add(path.toString());
                try {
                    refreshDirectories();
                } catch (IOException e) {
                    log.warn("Unable to refresh watched directories after ignore rules changed", e);
                }
                continue;
            }
            if (!path.startsWith(inputDirectory) || path.equals(inputDirectory)) {
                continue;
            }
            if (ignored(path)) {
                continue;
            }
            changed = true;
            changedPaths.add(path.toString());
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
                Set<String> changedPaths = new LinkedHashSet<>();
                boolean changed = process(watchService.take(), changedPaths);
                WatchKey key;
                while ((key = watchService.poll(250, TimeUnit.MILLISECONDS)) != null) {
                    changed |= process(key, changedPaths);
                }
                if (changed && !Thread.currentThread().isInterrupted()) {
                    log.info("Rebuilding site after source changes: {}", changedPaths);
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

    private record IgnoreRule(
        PathMatcher matcher,
        PathMatcher rootMatcher,
        boolean basenameOnly,
        boolean directoryOnly,
        boolean negated
    ) {

        private static Optional<IgnoreRule> parse(Path directory, String line) {
            String pattern = line.strip();
            if (pattern.isEmpty() || pattern.startsWith("#")) {
                return Optional.empty();
            }
            boolean negated = pattern.startsWith("!");
            if (negated) {
                pattern = pattern.substring(1);
            }
            if (pattern.isEmpty()) {
                return Optional.empty();
            }
            boolean directoryOnly = pattern.endsWith("/");
            boolean anchored = pattern.startsWith("/");
            pattern = pattern.replace('\\', '/');
            while (pattern.startsWith("/")) {
                pattern = pattern.substring(1);
            }
            if (directoryOnly) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            if (pattern.isEmpty()) {
                return Optional.empty();
            }
            boolean basenameOnly = !anchored && !pattern.contains("/");
            try {
                PathMatcher rootMatcher = pattern.startsWith("**/")
                    ? directory.getFileSystem().getPathMatcher("glob:" + pattern.substring(3))
                    : null;
                return Optional.of(new IgnoreRule(
                    directory.getFileSystem().getPathMatcher("glob:" + pattern),
                    rootMatcher,
                    basenameOnly,
                    directoryOnly,
                    negated));
            } catch (IllegalArgumentException e) {
                log.warn("Ignoring invalid .gitignore pattern '{}' in {}", line, directory.resolve(".gitignore"), e);
                return Optional.empty();
            }
        }

        private boolean matches(Path ruleDirectory, Path relativePath, Path path) {
            for (int depth = 0; depth < relativePath.getNameCount(); depth++) {
                Path candidate = relativePath.subpath(0, depth + 1);
                if (directoryOnly && depth == relativePath.getNameCount() - 1
                    && !Files.isDirectory(path)
                    && !Files.isDirectory(ruleDirectory.resolve(candidate))) {
                    continue;
                }
                if (basenameOnly
                    ? matcher.matches(candidate.getFileName())
                    : matcher.matches(candidate) || rootMatcher != null && rootMatcher.matches(candidate)) {
                    return true;
                }
            }
            return false;
        }
    }
}
