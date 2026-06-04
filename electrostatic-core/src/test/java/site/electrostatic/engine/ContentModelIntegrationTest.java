package site.electrostatic.engine;

import site.electrostatic.content.staticfiles.StaticFile;
import site.electrostatic.plugins.AggregatorPlugin;
import site.electrostatic.plugins.Plugins;
import j2html.TagCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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

    @Test
    void addFile_shouldPreferLocalAssetsOnConflictsAndExposeLocalCssInStableOrder() {
        ContentModel contentModel = new ContentModel();

        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errBuffer));
        try {
            contentModel.addFile(new StaticFile(
                "/css/style.css",
                Map.of(
                    "assetSourceType", List.of("classpath"),
                    "assetSourcePath", List.of("theme/default/css/style.css")
                ),
                "theme".getBytes(StandardCharsets.UTF_8)
            ));
            contentModel.addFile(new StaticFile(
                "/css/style.css",
                Map.of(
                    "assetSourceType", List.of("local"),
                    "assetSourcePath", List.of("/repo/_static/css/style.css")
                ),
                "local".getBytes(StandardCharsets.UTF_8)
            ));
            contentModel.addFile(new StaticFile(
                "css/styles-ext.css",
                Map.of(
                    "assetSourceType", List.of("local"),
                    "assetSourcePath", List.of("/repo/_static/css/styles-ext.css")
                ),
                "ext".getBytes(StandardCharsets.UTF_8)
            ));
        } finally {
            System.setErr(originalErr);
        }

        List<StaticFile> visitedFiles = new ArrayList<>();
        contentModel.visit(new ContentModelVisitor() {
            @Override
            public void page(Page page) {
            }

            @Override
            public void file(StaticFile file) {
                visitedFiles.add(file);
            }
        });

        assertEquals(2, visitedFiles.size());
        assertEquals("/css/style.css", visitedFiles.getFirst().getPath());
        assertEquals("local", new String(visitedFiles.getFirst().getRenderFunction().apply(new RenderModel()), StandardCharsets.UTF_8));
        assertEquals(List.of("/css/style.css", "/css/styles-ext.css"), contentModel.getLocalCssPaths());
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("Static asset conflict at /css/style.css"));
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("classpath:theme/default/css/style.css"));
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("local:/repo/_static/css/style.css"));
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
