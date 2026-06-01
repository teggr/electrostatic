package run.electrostatic.core;

import run.electrostatic.plugins.ThemePlugin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Shared preview server for serving generated sites locally.
 */
public class SitePreviewServer {

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
        siteGenerator.generate(inputDirectory, outputDirectory, options);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> serve(exchange, outputDirectory, projectRoot));
        server.start();

        return new PreviewSession(server, outputDirectory);
    }

    private static void serve(HttpExchange exchange, Path outputDirectory, Path projectRoot) throws IOException {
        try {
            String uri = exchange.getRequestURI().getPath();
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

        private PreviewSession(HttpServer server, Path outputDirectory) {
            this.server = server;
            this.outputDirectory = outputDirectory;
        }

        public Path getOutputDirectory() {
            return outputDirectory;
        }

        @Override
        public void close() {
            server.stop(0);
        }

    }

}