package run.electrostatic.core;

import run.electrostatic.theme.DefaultThemePlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SitePreviewServerIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void previewServerShouldServeGeneratedPagesAndProjectRootFallbackFiles() throws Exception {
        Path projectRoot = tempDir.resolve("preview-site");
        Path inputDirectory = projectRoot;
        Path outputDirectory = projectRoot.resolve("generated-site");

        new SiteInitializer(DefaultThemePlugin.create()).initialize(projectRoot);
        rewriteDefaultPostToDatedPost(projectRoot);
        Files.writeString(projectRoot.resolve("root-fallback.txt"), "root-content", StandardCharsets.UTF_8);

        int port = findAvailablePort();
        try (SitePreviewServer.PreviewSession session = new SitePreviewServer(DefaultThemePlugin.create())
            .start(inputDirectory, outputDirectory, null, port, projectRoot)) {
            waitForHttpSuccess("http://localhost:" + port + "/index.html", Duration.ofSeconds(30));

            HttpResponse outputResponse = httpGet("http://localhost:" + port + "/index.html");
            assertEquals(200, outputResponse.statusCode);
            assertTrue(outputResponse.body.contains("<html"));

            HttpResponse rootResponse = httpGet("http://localhost:" + port + "/root-fallback.txt");
            assertEquals(200, rootResponse.statusCode);
            assertEquals("root-content", rootResponse.body.trim());
        }
    }

    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void waitForHttpSuccess(String endpoint, Duration timeout) throws Exception {
        Instant deadline = Instant.now().plus(timeout);
        while (Instant.now().isBefore(deadline)) {
            try {
                HttpResponse response = httpGet(endpoint);
                if (response.statusCode == 200) {
                    return;
                }
            } catch (IOException ignored) {
                // Keep polling while the server starts.
            }
            Thread.sleep(250);
        }
        throw new IllegalStateException("Server did not become ready for endpoint: " + endpoint);
    }

    private static HttpResponse httpGet(String endpoint) throws Exception {
        URL url = URI.create(endpoint).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);

        int status = connection.getResponseCode();
        byte[] bodyBytes = status >= 400
            ? connection.getErrorStream() != null ? connection.getErrorStream().readAllBytes() : new byte[0]
            : connection.getInputStream().readAllBytes();
        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        return new HttpResponse(status, body);
    }

    private static void rewriteDefaultPostToDatedPost(Path siteRoot) throws Exception {
        Path postsDirectory = siteRoot.resolve("_posts");
        Files.deleteIfExists(postsDirectory.resolve("2026-01-01-hello-world.md"));
        Files.writeString(
            postsDirectory.resolve("2026-01-01-preview.md"),
            "---\n" +
                "title: Preview\n" +
                "author: test\n" +
                "---\n\n" +
                "Preview content.\n",
            StandardCharsets.UTF_8
        );
    }

    private record HttpResponse(int statusCode, String body) {
    }
}