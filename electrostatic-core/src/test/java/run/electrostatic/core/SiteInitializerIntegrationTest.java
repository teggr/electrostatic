package run.electrostatic.core;

import run.electrostatic.plugins.Plugins;
import run.electrostatic.theme.DefaultThemePlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteInitializerIntegrationTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
    }

    @AfterEach
    void tearDown() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
    }

    @Test
    void initialize_shouldCreateExpectedScaffoldInEmptyDirectory() throws Exception {
        Path root = tempDir.resolve("new-site");

        new SiteInitializer(DefaultThemePlugin.create()).initialize(root);

        assertTrue(Files.exists(root.resolve("site-config.xml")));
        assertTrue(Files.exists(root.resolve("_posts")));
        assertTrue(Files.exists(root.resolve("_posts/hello-world.md")));
        assertTrue(Files.exists(root.resolve("_books")));
        assertTrue(Files.exists(root.resolve("_podcasts")));
        assertTrue(Files.exists(root.resolve("_feeds")));
        assertTrue(Files.exists(root.resolve("_static")));
        assertTrue(Files.exists(root.resolve("_drafts")));

        String configXml = Files.readString(root.resolve("site-config.xml"));
        assertTrue(configXml.contains("<title>My Electrostatic Site</title>"));
        assertTrue(configXml.contains("<baseUrl>http://localhost:8080</baseUrl>"));
    }

    @Test
    void initialize_shouldFailWhenDirectoryIsNotEmpty() throws Exception {
        Path root = tempDir.resolve("existing-site");
        Files.createDirectories(root);
        Files.writeString(root.resolve("existing.txt"), "already here");

        assertThrows(
            IllegalStateException.class,
            () -> new SiteInitializer(DefaultThemePlugin.create()).initialize(root)
        );
    }
}
