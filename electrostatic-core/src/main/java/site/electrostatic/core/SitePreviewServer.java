package site.electrostatic.core;

import site.electrostatic.plugins.ThemePlugin;
import site.electrostatic.plugins.Plugins;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Shared preview server for serving generated sites locally.
 */
public class SitePreviewServer {

    private static final String RELOAD_PATH = "/__electrostatic/reload";
    private final SiteGenerator siteGenerator;

    public SitePreviewServer(ThemePlugin themePlugin) {
        this.siteGenerator = new SiteGenerator(Objects.requireNonNull(themePlugin, "themePlugin"));
    }

    public PreviewSession start(Path inputDirectory, Path outputDirectory, String baseUrl, int port, Path projectRoot)
        throws IOException {
        return start(inputDirectory, outputDirectory, GenerationOptions.fromBaseUrl(baseUrl), port, projectRoot);
    }

    public PreviewSession start(
        Path inputDirectory,
        Path outputDirectory,
        GenerationOptions options,
        int port,
        Path projectRoot
    ) throws IOException {
        return start(inputDirectory, outputDirectory, options, port, projectRoot, false);
    }

    public PreviewSession start(
        Path inputDirectory,
        Path outputDirectory,
        GenerationOptions options,
        int port,
        Path projectRoot,
        boolean watch
    ) throws IOException {
        Path input = inputDirectory.toAbsolutePath().normalize();
        Path output = outputDirectory.toAbsolutePath().normalize();
        Path root = projectRoot.toAbsolutePath().normalize();
        if (watch && input.startsWith(output)) {
            throw new IllegalArgumentException("Watch output directory must not contain the input directory");
        }
        Object generationLock = new Object();
        AtomicLong revision = new AtomicLong();
        SiteSourceWatcher watcher = watch ? new SiteSourceWatcher(input, output, () -> {
            synchronized (generationLock) {
                generateWatchedSite(input, output, options);
                revision.incrementAndGet();
            }
        }) : null;
        HttpServer server = null;
        try {
            if (watch) {
                generateWatchedSite(input, output, options);
            } else {
                siteGenerator.generate(input, output, options);
            }
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", exchange -> {
                synchronized (generationLock) {
                    serve(exchange, output, root, watch, revision.get());
                }
            });
            server.start();
            if (watcher != null) {
                watcher.start();
            }
            return new PreviewSession(server, output, watcher);
        } catch (Exception e) {
            if (watcher != null) {
                watcher.close();
            }
            if (server != null) {
                server.stop(0);
            }
            throw e;
        }
    }

    private void generateWatchedSite(Path input, Path output, GenerationOptions options) {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        siteGenerator.generate(input, output, options);
    }

    private static String reloadScript(long revision) {
        return """
            <script>
            (() => {
              const revision = '%d';
              setInterval(async () => {
                try {
                  const response = await fetch('%s', {cache: 'no-store'});
                  if (response.ok && (await response.text()) !== revision) location.reload();
                } catch (_) {}
              }, 1000);
            })();
            </script>
            """.formatted(revision, RELOAD_PATH);
    }

    private static void serve(HttpExchange exchange, Path outputDirectory, Path projectRoot,
                              boolean watch, long revision) throws IOException {
        try {
            String uri = exchange.getRequestURI().getPath();
            if (watch) {
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                if (RELOAD_PATH.equals(uri)) {
                    byte[] bytes = Long.toString(revision).getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream output = exchange.getResponseBody()) {
                        output.write(bytes);
                    }
                    return;
                }
            }
            if (uri.endsWith("/")) {
                uri += "index.html";
            }

            String relativeUri = uri.startsWith("/") ? uri.substring(1) : uri;
            File file = resolveFile(relativeUri, outputDirectory, projectRoot);
            if (file == null && shouldTryDirectoryIndex(relativeUri)) {
                file = resolveFile(relativeUri + "/index.html", outputDirectory, projectRoot);
            }
            if (file == null) {
                sendNotFound(exchange);
                return;
            }

            String contentType = URLConnection.guessContentTypeFromName(file.getName());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            if (watch && "text/html".equals(contentType)) {
                String html = new String(bytes, StandardCharsets.UTF_8);
                int bodyEnd = html.toLowerCase(java.util.Locale.ROOT).lastIndexOf("</body>");
                String script = reloadScript(revision);
                html = bodyEnd >= 0
                    ? html.substring(0, bodyEnd) + script + html.substring(bodyEnd)
                    : html + script;
                bytes = html.getBytes(StandardCharsets.UTF_8);
                contentType = "text/html; charset=utf-8";
            }
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        } finally {
            exchange.close();
        }
    }

    private static boolean shouldTryDirectoryIndex(String relativeUri) {
        if (relativeUri == null || relativeUri.isEmpty() || relativeUri.endsWith("/")) {
            return false;
        }

        int lastSlash = relativeUri.lastIndexOf('/');
        String lastSegment = lastSlash >= 0 ? relativeUri.substring(lastSlash + 1) : relativeUri;
        return !lastSegment.contains(".");
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        byte[] bytes = "Not Found".getBytes();
        exchange.sendResponseHeaders(404, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static File resolveFile(String relativeUri, Path outputDirectory, Path projectRoot) {
        File fromOutput = resolveFileInRoot(relativeUri, outputDirectory);
        if (fromOutput != null) {
            return fromOutput;
        }
        return resolveFileInRoot(relativeUri, projectRoot);
    }

    private static File resolveFileInRoot(String relativeUri, Path root) {
        try {
            Path resolved = root.resolve(relativeUri).normalize();
            if (!resolved.startsWith(root) || !Files.exists(resolved)) {
                return null;
            }

            Path realRoot = root.toRealPath();
            Path realResolved = resolved.toRealPath();
            if (!realResolved.startsWith(realRoot) || !Files.isRegularFile(realResolved)) {
                return null;
            }
            return realResolved.toFile();
        } catch (Exception e) {
            return null;
        }
    }

    public static final class PreviewSession implements AutoCloseable {

        private final HttpServer server;
        private final Path outputDirectory;
        private final SiteSourceWatcher watcher;

        private PreviewSession(HttpServer server, Path outputDirectory, SiteSourceWatcher watcher) {
            this.server = server;
            this.outputDirectory = outputDirectory;
            this.watcher = watcher;
        }

        public Path getOutputDirectory() {
            return outputDirectory;
        }

        @Override
        public void close() {
            if (watcher != null) {
                watcher.close();
            }
            server.stop(0);
        }

    }

}