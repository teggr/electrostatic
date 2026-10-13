package io.github.teggr.electrostatic.theme.includes;

import j2html.rendering.FlatHtml;
import org.junit.jupiter.api.Test;
import io.github.teggr.electrostatic.engine.ContentModel;
import io.github.teggr.electrostatic.engine.Context;
import io.github.teggr.electrostatic.engine.RenderModel;
import io.github.teggr.electrostatic.site.Author;
import io.github.teggr.electrostatic.site.Site;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeaderDocsNavigationTest {

    @Test
    void create_shouldRenderConfiguredDocsSectionsForPluginsProfile() throws Exception {
        RenderModel renderModel = createRenderModel("installation,guides,plugins", "installation=Installation,guides=Guides,plugins=Plugins");

        String html = Header.create(renderModel).render(FlatHtml.inMemory()).toString();

        assertTrue(html.contains("/installation/index.html"));
        assertTrue(html.contains("/guides/index.html"));
        assertTrue(html.contains("/plugins/index.html"));
        assertFalse(html.contains("/commands/index.html"));
    }

    @Test
    void create_shouldRenderConfiguredDocsSectionsForCommandsProfile() throws Exception {
        RenderModel renderModel = createRenderModel("installation,guides,commands", "installation=Installation,guides=Guides,commands=Commands");

        String html = Header.create(renderModel).render(FlatHtml.inMemory()).toString();

        assertTrue(html.contains("/installation/index.html"));
        assertTrue(html.contains("/guides/index.html"));
        assertTrue(html.contains("/commands/index.html"));
        assertFalse(html.contains("/plugins/index.html"));
    }

    private RenderModel createRenderModel(String sections, String labels) {
        Site site = new Site();
        site.setTitle("Docs Site");
        site.setAuthor(new Author("Docs Author", "docs@example.com"));
        site.setDocsSections(sections);
        site.setDocsSectionLabels(labels);

        Context context = new Context();
        context.setSite(site);

        RenderModel renderModel = new RenderModel();
        renderModel.setContext(context);
        renderModel.setContentModel(new ContentModel());
        return renderModel;
    }
}
