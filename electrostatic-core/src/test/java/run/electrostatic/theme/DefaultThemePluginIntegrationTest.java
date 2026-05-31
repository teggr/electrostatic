package run.electrostatic.theme;

import run.electrostatic.engine.ContentModel;
import run.electrostatic.engine.Layout;
import run.electrostatic.plugins.Plugins;
import run.electrostatic.site.Site;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultThemePluginIntegrationTest {

    @BeforeEach
    void setUp() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        System.setProperty("drafts", "false");
    }

    @AfterEach
    void tearDown() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        System.clearProperty("drafts");
    }

    @Test
    void registerPlugins_shouldWireDefaultThemeAcrossPluginRegistries() {
        DefaultThemePlugin.create().registerPlugins();

        assertEquals(9, Plugins.contentTypePlugins.size());
        assertEquals(4, Plugins.aggregatorPlugins.size());
        assertEquals(1, Plugins.contentRenderPlugins.size());
        assertTrue(Plugins.contentTypePlugins.stream().anyMatch(DefaultThemePlugin.class::isInstance));
    }

    @Test
    void loadContentAndLayouts_shouldProvideExpectedThemeArtifacts() {
        DefaultThemePlugin plugin = DefaultThemePlugin.create();
        ContentModel contentModel = new ContentModel();
        Map<String, Layout> layouts = new HashMap<>();

        plugin.loadContent(Path.of("."), new Site(), contentModel);
        plugin.loadLayout(layouts);

        List<String> pagePaths = contentModel.getPages().stream().map(page -> page.getPath()).toList();

        assertEquals(6, pagePaths.size());
        assertTrue(pagePaths.containsAll(List.of(
            "/404.html",
            "/tags/index.html",
            "/podcasts/index.html",
            "/books/index.html",
            "/feeds/index.html",
            "/posts/index.html"
        )));
        assertEquals(
            Set.of("default", "home", "page", "tag", "book", "podcast", "post", "posts"),
            layouts.keySet()
        );
    }
}
