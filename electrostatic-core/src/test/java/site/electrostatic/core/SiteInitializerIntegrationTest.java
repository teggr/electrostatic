package site.electrostatic.core;

import site.electrostatic.plugins.Plugins;
import site.electrostatic.theme.DefaultThemePlugin;
import site.electrostatic.theme.docs.DocsThemePlugin;
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
        assertTrue(Files.exists(root.resolve("_posts/2026-01-01-hello-world.md")));
        assertTrue(Files.exists(root.resolve("_books")));
        assertTrue(Files.exists(root.resolve("_podcasts")));
        assertTrue(Files.exists(root.resolve("_feeds")));
        assertTrue(Files.exists(root.resolve("_static")));
        assertTrue(Files.exists(root.resolve("_drafts")));

        String configXml = Files.readString(root.resolve("site-config.xml"));
        assertTrue(configXml.contains("<title>My Electrostatic Site</title>"));
        assertTrue(configXml.contains("<baseUrl>http://localhost:8080</baseUrl>"));
        assertTrue(configXml.contains("<theme>default</theme>"));
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

    @Test
    void initialize_shouldCreateDocsScaffoldForDocsTheme() throws Exception {
        Path root = tempDir.resolve("docs-site");

        new SiteInitializer(DocsThemePlugin.create()).initialize(root);

        assertTrue(Files.exists(root.resolve("site-config.xml")));
        assertTrue(Files.exists(root.resolve("_installation")));
        assertTrue(Files.exists(root.resolve("_guides")));
        assertTrue(Files.exists(root.resolve("_plugins")));
        assertTrue(Files.exists(root.resolve("_installation/getting-started.md")));
        assertTrue(Files.exists(root.resolve("_guides/first-guide.md")));
        assertTrue(Files.exists(root.resolve("_plugins/plugin-overview.md")));

        String configXml = Files.readString(root.resolve("site-config.xml"));
        assertTrue(configXml.contains("<theme>docs</theme>"));
        assertTrue(configXml.contains("<docsSections>installation,guides,plugins</docsSections>"));
    }
}
