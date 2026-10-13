package io.github.teggr.electrostatic.smoke;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenGoalSmokeIT {

    private static final String PAGES_BASE_URL = "https://example.github.io/smoke-project/";

    @TempDir
    Path tempDir;

    @Test
    void mavenGoalsShouldInitializeBuildAndServeAProjectSite() throws Exception {
        String configuredRepositoryRoot = System.getProperty("electrostatic.test.repositoryRoot");
        Path repositoryRoot = configuredRepositoryRoot == null || configuredRepositoryRoot.isBlank()
            ? null
            : Path.of(configuredRepositoryRoot).toAbsolutePath();
        assertTrue(repositoryRoot != null, "Repository root is not available from the smoke-test module");

        Path projectRoot = tempDir.resolve("maven-site");
        Files.createDirectories(projectRoot);
        Files.writeString(projectRoot.resolve("pom.xml"), """
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
              <modelVersion>4.0.0</modelVersion>
              <groupId>example</groupId>
              <artifactId>smoke-site</artifactId>
              <version>1.0-SNAPSHOT</version>
            </project>
            """, StandardCharsets.UTF_8);

        runMavenGoal(repositoryRoot, projectRoot, "init");
        Path siteRoot = projectRoot.resolve("src/main/resources/site");
        rewriteBaseUrl(siteRoot, PAGES_BASE_URL);
        rewriteDefaultPostToDatedPost(siteRoot);
        Files.writeString(
            siteRoot.resolve("_drafts/2026-01-02-draft-post.md"),
            """
                ---
                title: Draft Post
                author: smoke
                ---

                Draft body for Maven goal smoke test.
                """,
            StandardCharsets.UTF_8
        );

        runMavenGoal(repositoryRoot, projectRoot, "generate");
        Path defaultOutput = projectRoot.resolve("target/generated-site");
        String indexHtml = Files.readString(defaultOutput.resolve("index.html"), StandardCharsets.UTF_8);
        assertTrue(Files.exists(defaultOutput.resolve("css/main.css")));
        assertTrue(indexHtml.contains("href=\"/smoke-project/css/main.css\""));
        assertTrue(indexHtml.contains("site-header"));
        assertTrue(Files.readString(defaultOutput.resolve("feed.xml"), StandardCharsets.UTF_8).contains(PAGES_BASE_URL));
        assertTrue(treeContains(defaultOutput, "Smoke test post content."));
        assertTrue(treeDoesNotContain(defaultOutput, "Draft body for Maven goal smoke test."));

        Path overrideOutput = projectRoot.resolve("target/preview-site");
        runMavenGoal(
            repositoryRoot,
            projectRoot,
            "generate",
            "-Delectrostatic.outputDirectory=" + overrideOutput,
            "-Delectrostatic.baseUrl=http://localhost:8080",
            "-Delectrostatic.includeDrafts=true"
        );
        assertTrue(Files.readString(overrideOutput.resolve("feed.xml"), StandardCharsets.UTF_8).contains("http://localhost:8080"));
        assertTrue(treeContains(overrideOutput, "Draft body for Maven goal smoke test."));

        int port = findAvailablePort();
        Path servedOutput = projectRoot.resolve("target/served-site");
        Process serveProcess = startMavenGoal(
            repositoryRoot,
            projectRoot,
            "serve",
            "-Delectrostatic.port=" + port,
            "-Delectrostatic.outputDirectory=" + servedOutput,
            "-Delectrostatic.baseUrl=http://localhost:" + port
        );
        try {
            waitForHttpSuccess(
                "http://localhost:" + port + "/index.html",
                Duration.ofSeconds(60),
                serveProcess,
                projectRoot
            );
            HttpResponse response = httpGet("http://localhost:" + port + "/index.html");
            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("site-header"));
            assertTrue(Files.exists(servedOutput.resolve("css/main.css")));
        } finally {
            stopProcessTree(serveProcess);
        }
    }

    private static void runMavenGoal(Path repositoryRoot, Path projectRoot, String goal, String... options) throws Exception {
        Process process = startMavenGoal(repositoryRoot, projectRoot, goal, options);
        if (!process.waitFor(3, TimeUnit.MINUTES)) {
            stopProcessTree(process);
            throw new IllegalStateException("Maven goal timed out: " + goal + System.lineSeparator() + readMavenLog(projectRoot, goal));
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException(
                "Maven goal failed: " + goal + " (exit code " + process.exitValue() + ")" +
                    System.lineSeparator() + readMavenLog(projectRoot, goal)
            );
        }
    }

    private static Process startMavenGoal(
        Path repositoryRoot,
        Path projectRoot,
        String goal,
        String... options
    ) throws IOException {
        String version = System.getProperty("electrostatic.test.version");
        if (version == null || version.isBlank()) {
            throw new IllegalStateException("electrostatic.test.version was not configured for Maven goal smoke tests");
        }

        List<String> command = new ArrayList<>();
        Path wrapper = repositoryRoot.resolve(isWindows() ? "mvnw.cmd" : "mvnw");
        if (isWindows()) {
            command.add("cmd.exe");
            command.add("/d");
            command.add("/c");
            command.add("call");
        }
        command.add(wrapper.toString());
        command.add("--batch-mode");
        command.add("--no-transfer-progress");
        command.add("-f");
        command.add(projectRoot.resolve("pom.xml").toString());
        command.add("io.github.teggr:electrostatic-maven-plugin:" + version + ":" + goal);
        command.addAll(List.of(options));

        Path logFile = projectRoot.resolve("maven-" + goal + ".log");
        return new ProcessBuilder(command)
            .directory(projectRoot.toFile())
            .redirectErrorStream(true)
            .redirectOutput(logFile.toFile())
            .start();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static String readMavenLog(Path projectRoot, String goal) throws IOException {
        Path logFile = projectRoot.resolve("maven-" + goal + ".log");
        return Files.exists(logFile) ? Files.readString(logFile, StandardCharsets.UTF_8) : "Maven output log not found.";
    }

    private static void rewriteBaseUrl(Path siteRoot, String baseUrl) throws Exception {
        Path siteConfig = siteRoot.resolve("site-config.xml");
        String updated = Files.readString(siteConfig, StandardCharsets.UTF_8)
            .replace("<baseUrl>http://localhost:8080</baseUrl>", "<baseUrl>" + baseUrl + "</baseUrl>");
        Files.writeString(siteConfig, updated, StandardCharsets.UTF_8);
    }

    private static void rewriteDefaultPostToDatedPost(Path siteRoot) throws Exception {
        Path postsDirectory = siteRoot.resolve("_posts");
        Files.deleteIfExists(postsDirectory.resolve("hello-world.md"));
        Files.deleteIfExists(postsDirectory.resolve("2026-01-01-hello-world.md"));
        Files.writeString(
            postsDirectory.resolve("2026-01-01-smoke-test.md"),
            """
                ---
                title: Smoke Test Post
                author: smoke
                ---

                Smoke test post content.
                """,
            StandardCharsets.UTF_8
        );
    }

    private static boolean treeContains(Path root, String expectedText) throws IOException {
        try (var paths = Files.walk(root)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                if (Files.readString(path, StandardCharsets.UTF_8).contains(expectedText)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean treeDoesNotContain(Path root, String expectedText) throws IOException {
        return !treeContains(root, expectedText);
    }

    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void waitForHttpSuccess(
        String endpoint,
        Duration timeout,
        Process process,
        Path projectRoot
    ) throws Exception {
        Instant deadline = Instant.now().plus(timeout);
        while (Instant.now().isBefore(deadline)) {
            if (!process.isAlive()) {
                throw new IllegalStateException(
                    "Maven serve goal exited before the server was ready (exit code " + process.exitValue() + ")" +
                        System.lineSeparator() + readMavenLog(projectRoot, "serve")
                );
            }
            try {
                if (httpGet(endpoint).statusCode() == 200) {
                    return;
                }
            } catch (IOException ignored) {
                Thread.sleep(250);
            }
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
        connection.disconnect();
        return new HttpResponse(status, new String(bodyBytes, StandardCharsets.UTF_8));
    }

    private static void stopProcessTree(Process process) throws InterruptedException {
        ProcessHandle processHandle = process.toHandle();
        processHandle.descendants().forEach(ProcessHandle::destroyForcibly);
        processHandle.destroyForcibly();
        process.waitFor(10, TimeUnit.SECONDS);
    }

    private record HttpResponse(int statusCode, String body) {
    }
}
