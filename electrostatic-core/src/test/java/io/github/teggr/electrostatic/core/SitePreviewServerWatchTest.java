package io.github.teggr.electrostatic.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import io.github.teggr.electrostatic.content.staticfiles.StaticFile;
import io.github.teggr.electrostatic.plugins.AggregatorPlugin;
import io.github.teggr.electrostatic.plugins.ContentRenderPlugin;
import io.github.teggr.electrostatic.plugins.ContentTypePlugin;
import io.github.teggr.electrostatic.plugins.InitializationPlugin;
import io.github.teggr.electrostatic.plugins.Plugins;
import io.github.teggr.electrostatic.theme.docs.DocsThemePlugin;

import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SitePreviewServerWatchTest {

    @TempDir
    Path tempDir;

    private List<InitializationPlugin> initializationPlugins;
    private List<ContentTypePlugin> contentTypePlugins;
    private List<AggregatorPlugin> aggregatorPlugins;
    private List<ContentRenderPlugin> contentRenderPlugins;
    private final AtomicInteger builds = new AtomicInteger();
    private Path input;
    private Path output;
    private String endpoint;

    @BeforeEach
    void setUp() throws Exception {
        initializationPlugins = new ArrayList<>(Plugins.initializationPlugins);
        contentTypePlugins = new ArrayList<>(Plugins.contentTypePlugins);
        aggregatorPlugins = new ArrayList<>(Plugins.aggregatorPlugins);
        contentRenderPlugins = new ArrayList<>(Plugins.contentRenderPlugins);
        clearPlugins();
        input = tempDir.resolve("source");
        output = input.resolve("generated-site");
        new SiteInitializer(DocsThemePlugin.create()).initialize(input);
        clearPlugins();
        try (ServerSocket socket = new ServerSocket(0)) {
            endpoint = "http://localhost:" + socket.getLocalPort();
        }
    }

    @AfterEach
    void restorePlugins() {
        clearPlugins();
        Plugins.initializationPlugins.addAll(initializationPlugins);
        Plugins.contentTypePlugins.addAll(contentTypePlugins);
        Plugins.aggregatorPlugins.addAll(aggregatorPlugins);
        Plugins.contentRenderPlugins.addAll(contentRenderPlugins);
    }

    private static void clearPlugins() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
    }

    private SitePreviewServer.PreviewSession start(boolean watch) throws Exception {
        SitePreviewServer server = new SitePreviewServer(() -> {
            builds.incrementAndGet();
            DocsThemePlugin.create().registerPlugins();
            Plugins.contentTypePlugins.add((source, site, model) -> model.addFile(new StaticFile(
                "/options.txt", Map.of(), (site.getBaseUrl() + "|" + GenerationOptionsContext.current().includeDrafts())
                    .getBytes(StandardCharsets.UTF_8))));
        });
        GenerationOptions options = GenerationOptions.defaults().withBaseUrl(endpoint).withIncludeDrafts(true);
        return server.start(input, output, options, URI.create(endpoint).getPort(), input, watch);
    }

    @Test
    void watchShouldRebuildContentAndNestedAssetsAndKeepOptionsAndRegistriesStable() throws Exception {
        try (var session = start(true)) {
            String originalRevision = get("/__electrostatic/reload").body;
            Response html = get("/index.html");
            assertTrue(html.body.contains("fetch('/__electrostatic/reload'"));
            assertEquals("no-store", html.cacheControl);
            assertFalse(Files.readString(output.resolve("index.html")).contains("/__electrostatic/reload"));
            int pluginCount = Plugins.contentTypePlugins.size();

            Path guide = input.resolve("_guides/first-guide.md");
            Files.writeString(guide, "---\ntitle: Watched İ guide\n---\n\nUpdated content.\n");
            awaitRevisionChange(originalRevision);
            String updatedHtml = get("/guides/first-guide.html").body;
            assertTrue(updatedHtml.contains("Updated content."));
            assertTrue(updatedHtml.contains("</script>\n</body>"));
            assertEquals(endpoint + "|true", get("/options.txt").body);
            assertEquals(pluginCount, Plugins.contentTypePlugins.size());

            Path assets = Files.createDirectories(input.resolve("_static/new/nested"));
            Path css = assets.resolve("custom.css");
            Path image = assets.resolve("image.png");
            byte[] imageBytes = {0, 1, 2, 3, 4};
            String revision = get("/__electrostatic/reload").body;
            Files.writeString(css, "body { color: red; }");
            Files.write(image, imageBytes);
            await(() -> get("/new/nested/custom.css").body.contains("color: red"));
            assertArrayEquals(imageBytes, Files.readAllBytes(output.resolve("new/nested/image.png")));
            assertNotEquals(revision, get("/__electrostatic/reload").body);

            revision = get("/__electrostatic/reload").body;
            Files.writeString(css, "body { color: blue; }");
            awaitRevisionChange(revision);
            assertTrue(get("/new/nested/custom.css").body.contains("color: blue"));

            revision = get("/__electrostatic/reload").body;
            Files.delete(css);
            Files.move(image, assets.resolve("renamed.png"));
            awaitRevisionChange(revision);
            assertEquals(404, get("/new/nested/custom.css").status);
            assertEquals(404, get("/new/nested/image.png").status);
            assertArrayEquals(imageBytes, Files.readAllBytes(output.resolve("new/nested/renamed.png")));
        }
    }

    @Test
    void watchShouldIgnoreOutputAndGitAndStopWhenSessionCloses() throws Exception {
        SitePreviewServer.PreviewSession session = start(true);
        try {
            int before = builds.get();
            Files.writeString(output.resolve("ignored.txt"), "generated");
            Path git = Files.createDirectories(input.resolve(".git/objects"));
            Files.writeString(git.resolve("ignored"), "metadata");
            assertStableBuildCount(before, Duration.ofMillis(800));

            Files.writeString(input.resolve(".gitignore"), "**/target/\n*.swp\n");
            int beforeIgnoreFileChange = builds.get();
            await(() -> builds.get() > beforeIgnoreFileChange);
            assertStableBuildCount(builds.get(), Duration.ofMillis(300));
            before = builds.get();
            Files.createDirectories(input.resolve("target/classes"));
            Files.writeString(input.resolve("target/classes/build-info"), "generated");
            Files.writeString(input.resolve("editor.swp"), "temporary");
            assertStableBuildCount(before, Duration.ofMillis(800));
        } finally {
            session.close();
        }
        int before = builds.get();
        Files.writeString(input.resolve("_index.md"), "Changed after closing.");
        assertStableBuildCount(before, Duration.ofMillis(500));
    }

    @Test
    void watchShouldRecoverAfterFailedBuildWithoutNotifyingBrowser() throws Exception {
        try (var session = start(true)) {
            Path config = input.resolve("site-config.xml");
            String validConfig = Files.readString(config);
            String revision = get("/__electrostatic/reload").body;
            int before = builds.get();
            Files.writeString(config, "<site>");
            await(() -> builds.get() > before);
            assertEquals(revision, get("/__electrostatic/reload").body);
            assertEquals(200, get("/index.html").status);

            Files.writeString(config, validConfig.replace("My Electrostatic Docs", "Recovered site"));
            awaitRevisionChange(revision);
            assertTrue(get("/index.html").body.contains("Recovered site"));
        }
    }

    @Test
    void serveWithoutWatchShouldNotInjectReloadOrRebuild() throws Exception {
        try (var session = start(false)) {
            assertFalse(get("/index.html").body.contains("/__electrostatic/reload"));
            assertEquals(404, get("/__electrostatic/reload").status);
            int before = builds.get();
            Files.writeString(input.resolve("_index.md"), "No automatic rebuild.");
            assertStableBuildCount(before, Duration.ofMillis(500));
            assertFalse(get("/index.html").body.contains("No automatic rebuild."));
        }
    }

    @Test
    void watchShouldRejectOutputThatContainsSource() {
        assertThrows(IllegalArgumentException.class, () ->
            new SitePreviewServer(DocsThemePlugin.create()).start(
                input, input, GenerationOptions.defaults(), 0, input, true));
        assertTrue(Files.exists(input.resolve("site-config.xml")));
    }

    private void awaitRevisionChange(String revision) throws Exception {
        await(() -> !revision.equals(get("/__electrostatic/reload").body));
    }

    private static void await(Callable<Boolean> condition) throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
        while (System.nanoTime() < deadline) {
            if (condition.call()) {
                return;
            }
            Thread.sleep(20);
        }
        fail("Timed out waiting for preview rebuild");
    }

    private void assertStableBuildCount(int expected, Duration duration) throws Exception {
        long deadline = System.nanoTime() + duration.toNanos();
        while (System.nanoTime() < deadline) {
            assertEquals(expected, builds.get());
            Thread.sleep(20);
        }
    }

    private Response get(String path) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) URI.create(endpoint + path).toURL().openConnection();
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(5000);
        try {
            int status = connection.getResponseCode();
            var stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String body = stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return new Response(status, body, connection.getHeaderField("Cache-Control"));
        } finally {
            connection.disconnect();
        }
    }

    private record Response(int status, String body, String cacheControl) {
    }
}
