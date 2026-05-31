package run.electrostatic.docs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import run.electrostatic.engine.ContentModel;
import run.electrostatic.site.Site;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocsCollectionPluginIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void loadContent_shouldSupportGenericSectionNamesFromSiteConfig() throws Exception {
        Files.createDirectories(tempDir.resolve("_guides"));
        Files.createDirectories(tempDir.resolve("_commands"));

        Files.writeString(tempDir.resolve("_guides/getting-started.md"), """
            ---
            title: Getting Started
            description: Start here
            order: 2
            ---

            Guide content.
            """);

        Files.writeString(tempDir.resolve("_commands/deploy.md"), """
            ---
            title: Deploy Command
            description: Deploy docs
            order: 1
            ---

            Command content.
            """);

        Site site = new Site();
        site.setDocsSections("guides,commands");
        site.setDocsSectionLabels("guides=Guides,commands=Commands");

        ContentModel contentModel = new ContentModel();
        DocsCollectionPlugin.create().loadContent(tempDir, site, contentModel);

        List<DocsEntry> entries = contentModel.getContentOfType(DocsEntry.class);
        List<String> paths = contentModel.getPages().stream().map(page -> page.getPath()).toList();

        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(entry -> "/guides/getting-started.html".equals(entry.getUrl())));
        assertTrue(entries.stream().anyMatch(entry -> "/commands/deploy.html".equals(entry.getUrl())));

        assertTrue(paths.contains("/guides/index.html"));
        assertTrue(paths.contains("/commands/index.html"));
    }
}
