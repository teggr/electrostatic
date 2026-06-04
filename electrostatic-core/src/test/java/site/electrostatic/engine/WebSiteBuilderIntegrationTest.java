package site.electrostatic.engine;

import site.electrostatic.core.GenerationOptions;
import site.electrostatic.content.staticfiles.StaticFile;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.plugins.ThemePlugin;
import site.electrostatic.theme.DefaultThemePlugin;
import site.electrostatic.theme.docs.DocsThemePlugin;
import j2html.TagCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSiteBuilderIntegrationTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        System.clearProperty("workingDirectory");
        System.clearProperty("environment");
    }

    @AfterEach
    void tearDown() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        System.clearProperty("workingDirectory");
        System.clearProperty("environment");
    }

    @Test
    void build_shouldWireCoreComponentsAndProduceFreshOutput() throws Exception {
        Path staleFile = tempDir.resolve("target/site/stale.txt");
        Files.createDirectories(staleFile.getParent());
        Files.writeString(staleFile, "stale output");

        Files.writeString(tempDir.resolve("site-config.xml"), """
            <site>
              <title>Test Site</title>
              <baseUrl>https://example.test</baseUrl>
            </site>
            """);

        System.setProperty("workingDirectory", tempDir.toString());
        System.setProperty("environment", "test");

        ThemePlugin themePlugin = new TestThemePlugin();
        new WebSiteBuilder(themePlugin).build("https://override.example");

        Path renderedPage = tempDir.resolve("target/site/index.html");
        Path renderedAsset = tempDir.resolve("target/site/assets/message.txt");

        assertFalse(Files.exists(staleFile));
        assertTrue(Files.exists(renderedPage));
        assertTrue(Files.exists(renderedAsset));

        String html = Files.readString(renderedPage);
        assertTrue(html.contains("Generated page"));
        assertTrue(html.contains("https://override.example"));
        assertEquals("asset-content", Files.readString(renderedAsset));
    }

    @Test
    void build_withIncludeDraftsOption_shouldRenderDraftContentWithoutSystemProperty() throws Exception {
        Files.writeString(tempDir.resolve("site-config.xml"), """
            <site>
              <title>Test Site</title>
              <baseUrl>https://example.test</baseUrl>
              <theme>default</theme>
                            <author>
                                <name>Test Author</name>
                            </author>
            </site>
            """);
        Files.createDirectories(tempDir.resolve("_books"));
        Files.createDirectories(tempDir.resolve("_podcasts"));
        Files.createDirectories(tempDir.resolve("_feeds"));
        Files.createDirectories(tempDir.resolve("_static"));
        Files.createDirectories(tempDir.resolve("_posts"));
        Files.createDirectories(tempDir.resolve("_drafts"));
        Files.writeString(tempDir.resolve("_drafts/2026-01-02-draft-post.md"), """
            ---
            title: Draft Post
            author: test
            ---

            Draft body from the explicit includeDrafts option.
            """);

        System.clearProperty("drafts");

        Path outputDirectory = tempDir.resolve("generated-site");
        GenerationOptions options = GenerationOptions.fromBaseUrl(null).withIncludeDrafts(true);

        new WebSiteBuilder(DefaultThemePlugin.create()).build(options, tempDir, outputDirectory);

        assertTrue(treeContains(outputDirectory, "Draft body from the explicit includeDrafts option."));
    }

    @Test
    void build_withDocsThemeAndSubpathBaseUrl_shouldPrefixAssetsAndNavLinks() throws Exception {
        Files.writeString(tempDir.resolve("site-config.xml"), """
            <site>
              <title>Docs Site</title>
              <baseUrl>https://teggr.github.io/ci-ready-maven/</baseUrl>
              <theme>docs</theme>
              <description>Project documentation</description>
              <author>
                <name>Docs Author</name>
              </author>
              <docsSections>installation,guides,plugins</docsSections>
              <docsSectionLabels>installation=Installation,guides=Guides,plugins=Plugins</docsSectionLabels>
            </site>
            """);
        Files.createDirectories(tempDir.resolve("_installation"));
        Files.createDirectories(tempDir.resolve("_guides"));
        Files.createDirectories(tempDir.resolve("_plugins"));
        Files.createDirectories(tempDir.resolve("_static"));
        Files.writeString(tempDir.resolve("_index.md"), """
            ---
            title: Docs Site
            ---

            [Plugins root relative](/plugins/index.html)
            [Plugins page relative](plugins/index.html)
            [Guides absolute path](/guides/index.html)
            """);
        Files.writeString(tempDir.resolve("_installation/getting-started.md"), """
            ---
            title: Getting Started
            ---
            Install docs.
            """);
        Files.writeString(tempDir.resolve("_guides/first-guide.md"), """
            ---
            title: First Guide
            ---
            Guide docs.
            """);
        Files.writeString(tempDir.resolve("_plugins/plugin-overview.md"), """
            ---
            title: Plugin Overview
            ---
            Plugin docs.
            [Docs collection plugin](docs-collection-plugin.html)
            [Guides absolute path](/guides/index.html)
            """);

        Path outputDirectory = tempDir.resolve("generated-site");
        new WebSiteBuilder(DocsThemePlugin.create()).build(GenerationOptions.defaults(), tempDir, outputDirectory);

        String html = Files.readString(outputDirectory.resolve("index.html"));
        String pluginsIndexHtml = Files.readString(outputDirectory.resolve("plugins/index.html"));
        String pluginOverviewHtml = Files.readString(outputDirectory.resolve("plugins/plugin-overview.html"));
        assertTrue(html.contains("href=\"/ci-ready-maven/css/main.css\""));
        assertTrue(html.contains("href=\"/ci-ready-maven/css/style.css\""));
        assertTrue(html.contains("href=\"/ci-ready-maven/\""));
        assertTrue(html.contains("href=\"/ci-ready-maven/guides/index.html\""));
        assertTrue(html.contains("<a href=\"/ci-ready-maven/plugins/index.html\">Plugins root relative</a>"));
        assertTrue(html.contains("<a href=\"/ci-ready-maven/plugins/index.html\">Plugins page relative</a>"));
        assertTrue(html.contains("<a href=\"/ci-ready-maven/guides/index.html\">Guides absolute path</a>"));
        assertTrue(pluginsIndexHtml.contains("href=\"/ci-ready-maven/plugins/plugin-overview.html\""));
        assertTrue(pluginOverviewHtml.contains("<a href=\"/ci-ready-maven/plugins/docs-collection-plugin.html\">Docs collection plugin</a>"));
        assertTrue(pluginOverviewHtml.contains("<a href=\"/ci-ready-maven/guides/index.html\">Guides absolute path</a>"));
        assertTrue(html.contains("rel=\"canonical\" href=\"https://teggr.github.io/ci-ready-maven/index.html\""));
        assertFalse(html.contains("ci-ready-maven//"));
    }

    @Test
    void build_withDocsThemeAndLandingPageOverride_shouldRenderAuthoredRootContentAndSectionCards() throws Exception {
        Files.writeString(tempDir.resolve("site-config.xml"), """
            <site>
              <title>Docs Site</title>
              <baseUrl>https://example.test</baseUrl>
              <theme>docs</theme>
              <description>Project documentation</description>
              <author>
                <name>Docs Author</name>
              </author>
              <docsSections>installation,guides,plugins</docsSections>
              <docsSectionLabels>installation=Installation,guides=Guides,plugins=Plugins</docsSectionLabels>
            </site>
            """);
        Files.createDirectories(tempDir.resolve("_installation"));
        Files.createDirectories(tempDir.resolve("_guides"));
        Files.createDirectories(tempDir.resolve("_plugins"));
        Files.createDirectories(tempDir.resolve("_static"));
        Files.writeString(tempDir.resolve("_installation/getting-started.md"), """
            ---
            title: Getting Started
            ---
            Install docs.
            """);
        Files.writeString(tempDir.resolve("_guides/first-guide.md"), """
            ---
            title: First Guide
            ---
            Guide docs.
            """);
        Files.writeString(tempDir.resolve("_plugins/plugin-overview.md"), """
            ---
            title: Plugin Overview
            ---
            Plugin docs.
            """);
        Files.writeString(tempDir.resolve("_index.md"), """
            ---
            title: CI Ready Maven
            description: Project-specific landing page
            ---

            Build docs for your own project.

            - Install the plugin
            - Publish your site
            """);

        Path outputDirectory = tempDir.resolve("generated-site");
        new WebSiteBuilder(DocsThemePlugin.create()).build(GenerationOptions.defaults(), tempDir, outputDirectory);

        String html = Files.readString(outputDirectory.resolve("index.html"));
        assertTrue(html.contains("<title>CI Ready Maven | Docs Site</title>"));
        assertTrue(html.contains("<h1>CI Ready Maven</h1>"));
        assertTrue(html.contains("<p>Build docs for your own project.</p>"));
        assertTrue(html.contains("<li>Install the plugin</li>"));
        assertFalse(html.contains("Build and publish documentation sites with Electrostatic."));
        assertTrue(html.contains(">Browse</a>"));
        assertTrue(html.contains("href=\"/guides/index.html\""));
    }

    @Test
    void build_withDocsTheme_shouldAutoLinkAllLocalCssAfterThemeCssAndPreferLocalConflicts() throws Exception {
        Files.writeString(tempDir.resolve("site-config.xml"), """
            <site>
              <title>Docs Site</title>
              <baseUrl>https://example.test</baseUrl>
              <theme>docs</theme>
              <description>Project documentation</description>
              <author>
                <name>Docs Author</name>
              </author>
              <docsSections>installation</docsSections>
              <docsSectionLabels>installation=Installation</docsSectionLabels>
            </site>
            """);
        Files.createDirectories(tempDir.resolve("_installation"));
        Files.writeString(tempDir.resolve("_installation/getting-started.md"), """
            ---
            title: Getting Started
            ---
            Install docs.
            """);
        Files.createDirectories(tempDir.resolve("_static/css"));
        Files.writeString(tempDir.resolve("_static/css/style.css"), ".local-style{color:green;}");
        Files.writeString(tempDir.resolve("_static/css/styles-ext.css"), ".local-ext{color:blue;}");

        Path outputDirectory = tempDir.resolve("generated-site");
        new WebSiteBuilder(DocsThemePlugin.create()).build(GenerationOptions.defaults(), tempDir, outputDirectory);

        String html = Files.readString(outputDirectory.resolve("index.html"));
        assertTrue(html.contains("href=\"/css/main.css\""));
        assertTrue(html.contains("href=\"/css/style.css\""));
        assertTrue(html.contains("href=\"/css/styles-ext.css\""));
        int styleCssIndex = html.indexOf("href=\"/css/style.css\"");
        int stylesExtCssIndex = html.indexOf("href=\"/css/styles-ext.css\"");
        assertTrue(styleCssIndex >= 0);
        assertTrue(stylesExtCssIndex >= 0);
        assertTrue(styleCssIndex < stylesExtCssIndex);
        assertEquals(".local-style{color:green;}", Files.readString(outputDirectory.resolve("css/style.css")));
    }

    private static boolean treeContains(Path root, String expectedText) throws Exception {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                .filter(Files::isRegularFile)
                .anyMatch(path -> containsText(path, expectedText));
        }
    }

    private static boolean containsText(Path path, String expectedText) {
        try {
            return Files.readString(path).contains(expectedText);
        } catch (Exception ignored) {
            return false;
        }
    }

    private static class TestThemePlugin implements ThemePlugin {
        @Override
        public void registerPlugins() {
            Plugins.contentTypePlugins.add((sourceDirectory, site, contentModel) -> {
                contentModel.addPage(Page.builder()
                    .path("/index.html")
                    .data(Map.of("layout", List.of("default")))
                    .renderFunction(renderModel -> TagCreator.div(
                        TagCreator.h1("Generated page"),
                        TagCreator.p(renderModel.getContext().getSite().getBaseUrl())
                    ))
                    .build());
                contentModel.addFile(new StaticFile("/assets/message.txt", Map.of(), "asset-content".getBytes()));
            });

            Plugins.contentRenderPlugins.add(layouts -> layouts.put("default", Layout.builder()
                .data(Map.of())
                .renderFunction(renderModel -> TagCreator.html(
                    TagCreator.body(renderModel.getContent())
                ))
                .build()));
        }
    }
}
