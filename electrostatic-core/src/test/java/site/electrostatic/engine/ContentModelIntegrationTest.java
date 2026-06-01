package site.electrostatic.engine;

import site.electrostatic.content.staticfiles.StaticFile;
import site.electrostatic.plugins.AggregatorPlugin;
import site.electrostatic.plugins.Plugins;
import j2html.TagCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentModelIntegrationTest {

    @BeforeEach
    void setUp() {
        Plugins.aggregatorPlugins.clear();
    }

    @AfterEach
    void tearDown() {
        Plugins.aggregatorPlugins.clear();
    }

    @Test
    void addAndVisit_shouldFlowContentAcrossCoreModelAndAggregators() {
        RecordingAggregatorPlugin aggregator = new RecordingAggregatorPlugin();
        Plugins.aggregatorPlugins.add(aggregator);

        ContentModel contentModel = new ContentModel();
        contentModel.addPage(Page.builder()
            .path("/manual/index.html")
            .data(Map.of("layout", List.of("default")))
            .renderFunction(renderModel -> TagCreator.div("manual-page"))
            .build());
        contentModel.addFile(new StaticFile("/assets/site.css", Map.of(), "body {}".getBytes()));

        TestContentItem item = new TestContentItem("/posts/post-1/index.html", Map.of("title", List.of("Post One")));
        contentModel.add(item);

        List<String> visitedPages = new ArrayList<>();
        List<String> visitedFiles = new ArrayList<>();

        contentModel.visit(new ContentModelVisitor() {
            @Override
            public void page(Page page) {
                visitedPages.add(page.getPath());
            }

            @Override
            public void file(StaticFile file) {
                visitedFiles.add(file.getPath());
            }
        });

        assertEquals(1, aggregator.addedItems.size());
        assertTrue(contentModel.getContentOfType(TestContentItem.class).contains(item));
        assertEquals(
            List.of("/manual/index.html", "/posts/post-1/index.html", "/aggregated/index.html"),
            visitedPages
        );
        assertEquals(List.of("/assets/site.css"), visitedFiles);
    }

    private static class RecordingAggregatorPlugin implements AggregatorPlugin {
        private final List<ContentItem> addedItems = new ArrayList<>();

        @Override
        public void visit(ContentModelVisitor visitor) {
            visitor.page(Page.builder()
                .path("/aggregated/index.html")
                .data(Map.of("layout", List.of("default")))
                .renderFunction(renderModel -> TagCreator.div("aggregated-page"))
                .build());
        }

        @Override
        public void add(ContentItem contentItem) {
            addedItems.add(contentItem);
        }
    }

    private record TestContentItem(String url, Map<String, List<String>> data) implements ContentItem {
        @Override
        public Map<String, List<String>> getData() {
            return data;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public j2html.tags.DomContent getContent(RenderModel renderModel) {
            return TagCreator.article("content-item");
        }
    }
}
