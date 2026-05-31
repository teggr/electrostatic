package com.robintegg.web.engine;

import com.robintegg.web.content.staticfiles.StaticFile;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.plugins.ThemePlugin;
import com.robintegg.web.site.Site;
import j2html.TagCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSiteBuilderIntegrationTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        System.clearProperty("workingDirectory");
        System.clearProperty("environment");
    }

    @AfterEach
    void tearDown() {
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
