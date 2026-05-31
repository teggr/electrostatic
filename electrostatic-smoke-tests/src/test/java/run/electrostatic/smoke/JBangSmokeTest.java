package run.electrostatic.smoke;

import org.junit.jupiter.api.Assumptions;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JBangSmokeTest {

    @TempDir
    Path tempDir;

    @Test
    void jbangInitBuildAndServeShouldGenerateAndServeFromOutputAndRoot() throws Exception {
        Path repoRoot = Path.of("").toAbsolutePath().getParent();
        Assumptions.assumeTrue(repoRoot != null, "Repository root is not available from test working directory");
        String jbangCommand = resolveJbangCommand();

        Path scriptPath = repoRoot.resolve("Electrostatic.java");
        Assumptions.assumeTrue(Files.exists(scriptPath), "Electrostatic.java not found in repository root");

        Path siteRoot = tempDir.resolve("jbang-site");
        Files.createDirectories(siteRoot);

        runCommand(List.of(jbangCommand, scriptPath.toString(), "init"), siteRoot);
        rewriteDefaultPostToDatedPost(siteRoot);

        Path rootFallbackFile = siteRoot.resolve("root-fallback.txt");
        Files.writeString(rootFallbackFile, "root-content", StandardCharsets.UTF_8);

        runCommand(List.of(jbangCommand, scriptPath.toString(), "build"), siteRoot);

        Path outputDir = siteRoot.resolve("generated-site");
        assertTrue(Files.isDirectory(outputDir));
        assertTrue(Files.exists(outputDir.resolve("index.html")));

        int port = findAvailablePort();
        Process serveProcess = startProcess(List.of(jbangCommand, scriptPath.toString(), "serve", "--port", String.valueOf(port)), siteRoot);
        try {
            waitForHttpSuccess("http://localhost:" + port + "/index.html", Duration.ofSeconds(30));

            HttpResponse outputResponse = httpGet("http://localhost:" + port + "/index.html");
            assertEquals(200, outputResponse.statusCode);
            assertTrue(outputResponse.body.contains("<html"));

            HttpResponse rootResponse = httpGet("http://localhost:" + port + "/root-fallback.txt");
            assertEquals(200, rootResponse.statusCode);
            assertEquals("root-content", rootResponse.body.trim());
        } finally {
            stopProcessTree(serveProcess);
        }
    }

    private static String resolveJbangCommand() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        return osName.contains("win") ? "jbang.cmd" : "jbang";
    }

    private static void runCommand(List<String> command, Path workingDirectory) throws Exception {
        Process process = startProcess(command, workingDirectory);
        boolean finished = process.waitFor(2, java.util.concurrent.TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("Command timed out: " + String.join(" ", command));
        }

        if (process.exitValue() != 0) {
            throw new IllegalStateException("Command failed with exit code " + process.exitValue() + ": " + String.join(" ", command));
        }
    }

    private static Process startProcess(List<String> command, Path workingDirectory) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command)
            .directory(workingDirectory.toFile())
            .inheritIO();

        try {
            return processBuilder.start();
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("Cannot run program")) {
                Assumptions.abort("JBang is not available in PATH");
            }
            throw e;
        }
    }

    private static void stopProcessTree(Process process) throws Exception {
        ProcessHandle processHandle = process.toHandle();
        processHandle.descendants().forEach(ProcessHandle::destroyForcibly);
        processHandle.destroyForcibly();
        process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
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

    private static void rewriteDefaultPostToDatedPost(Path siteRoot) throws Exception {
        Path postsDirectory = siteRoot.resolve("_posts");
        Files.deleteIfExists(postsDirectory.resolve("hello-world.md"));
        Files.writeString(
            postsDirectory.resolve("2026-01-01-smoke-test.md"),
            "---\n" +
                "title: Smoke Test Post\n" +
                "author: smoke\n" +
                "---\n\n" +
                "Smoke test post content.\n",
            StandardCharsets.UTF_8
        );
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

    private record HttpResponse(int statusCode, String body) {
    }
}
