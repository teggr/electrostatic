package com.robintegg.web.theme.v2.layouts;

import com.robintegg.web.content.IndexContent;
import com.robintegg.web.content.post.Post;
import com.robintegg.web.engine.Context;
import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.Pageable;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.index.IndexPlugin;
import com.robintegg.web.site.Author;
import com.robintegg.web.site.Site;
import com.robintegg.web.theme.layouts.PagedContent;
import com.robintegg.web.theme.pages.IndexPage;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class V2HomeLayoutTest {

    private final Parser parser = Parser.builder().build();

    @BeforeEach
    void setUp() throws Exception {
        clearIndexedContent();
    }

    @Test
    void getHomePagedContent_shouldFeatureLatestAndAvoidDuplicatesAcrossPages() {
        for (int day = 1; day <= 12; day++) {
            IndexPlugin.INSTANCE.add(createPost(day));
        }

        IndexContent latestContent = IndexPlugin.INSTANCE.getLatestIndexedContent().orElseThrow();
        PagedContent<IndexContent> pageOne = IndexPlugin.INSTANCE.getHomePagedContent(new Pageable(1, 10));
        PagedContent<IndexContent> pageTwo = IndexPlugin.INSTANCE.getHomePagedContent(new Pageable(2, 10));

        assertEquals("Post 12", latestContent.getTitle());
        assertEquals(10, pageOne.getContent().size());
        assertEquals("Post 11", pageOne.getContent().get(0).getTitle());
        assertEquals("Post 2", pageOne.getContent().get(9).getTitle());
        assertEquals(1, pageTwo.getContent().size());
        assertEquals("Post 1", pageTwo.getContent().get(0).getTitle());
        assertEquals(2, pageOne.getTotalPages());
        assertFalse(pageOne.getContent().stream().anyMatch(post -> post.getTitle().equals(latestContent.getTitle())));
        assertFalse(pageTwo.getContent().stream().anyMatch(post -> post.getTitle().equals("Post 2")));
    }

    @Test
    void render_shouldShowFeaturedPostOnFirstPageWithoutLegacyIntro() {
        for (int day = 1; day <= 12; day++) {
            IndexPlugin.INSTANCE.add(createPost(day));
        }

        RenderModel renderModel = createRenderModel(IndexPage.create("/index.html", new Pageable(1, 10)));

        String html = V2HomeLayout.render(renderModel).render();

        assertTrue(html.contains("featured-post"));
        assertTrue(html.contains("Post 12"));
        assertTrue(html.contains("Description for post 12"));
        assertTrue(html.contains("Read article →"));
        assertTrue(html.contains("Post 11"));
        assertFalse(html.contains("blog-intro"));
        assertFalse(html.contains("post-card-title\">Post 12</a>"));
    }

    private RenderModel createRenderModel(Page page) {
        RenderModel renderModel = new RenderModel();
        renderModel.setPage(page);

        Context context = new Context();
        Site site = new Site();
        site.setBaseUrl("http://example.com");
        site.setAuthor(new Author("Robin Tegg", null));
        context.setSite(site);
        renderModel.setContext(context);

        return renderModel;
    }

    private Post createPost(int day) {
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Post " + day));
        data.put("date", List.of("2026-03-" + String.format("%02d", day)));
        data.put("description", List.of("Description for post " + day));
        data.put("image", List.of("/images/post-" + day + ".png"));
        data.put("tags", List.of("ai", "workflow"));

        Node document = parser.parse("Paragraph for post " + day + ".");
        return new Post("2026-03-" + String.format("%02d", day) + "-post-" + day, data, document);
    }

    @SuppressWarnings("unchecked")
    private void clearIndexedContent() throws Exception {
        Field field = IndexPlugin.class.getDeclaredField("indexedContentList");
        field.setAccessible(true);
        field.set(IndexPlugin.INSTANCE, new ArrayList<>());
    }
}