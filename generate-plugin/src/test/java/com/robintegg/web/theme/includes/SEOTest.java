package com.robintegg.web.theme.includes;

import com.robintegg.web.engine.Context;
import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.site.Author;
import com.robintegg.web.site.Site;
import j2html.tags.DomContent;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SEOTest {

    @Test
    void testRenderWithImageMetadata() {
        // Setup test data
        Map<String, List<String>> pageData = new HashMap<>();
        pageData.put("title", List.of("Test Post"));
        pageData.put("image", List.of("/images/test.jpg"));
        pageData.put("image_width", List.of("1200"));
        pageData.put("image_height", List.of("630"));
        pageData.put("image_alt", List.of("Test image description"));

        Page page = Page.builder()
                .path("/2026/01/30/test-post.html")
                .data(pageData)
                .build();

        Author author = new Author();
        author.setName("Test Author");

        Site site = new Site();
        site.setTitle("Test Site");
        site.setDescription("Test Description");
        site.setBaseUrl("https://example.com");
        site.setAuthor(author);
        site.setTwitterUsername("testuser");

        Context context = new Context();
        context.setSite(site);

        RenderModel renderModel = new RenderModel();
        renderModel.setPage(page);
        renderModel.setContext(context);

        // Render SEO meta tags
        DomContent seoContent = SEO.render(renderModel);
        String html = seoContent.render();

        // Verify OpenGraph image metadata
        assertTrue(html.contains("<meta property=\"og:image\" content=\"https://example.com/images/test.jpg\">"),
                "Should contain og:image meta tag");
        assertTrue(html.contains("<meta property=\"og:image:width\" content=\"1200\">"),
                "Should contain og:image:width meta tag");
        assertTrue(html.contains("<meta property=\"og:image:height\" content=\"630\">"),
                "Should contain og:image:height meta tag");
        assertTrue(html.contains("<meta property=\"og:image:alt\" content=\"Test image description\">"),
                "Should contain og:image:alt meta tag");

        // Verify Twitter image alt metadata
        assertTrue(html.contains("<meta name=\"twitter:image:alt\" content=\"Test image description\">"),
                "Should contain twitter:image:alt meta tag");
        
        // Verify Twitter card is summary_large_image
        assertTrue(html.contains("<meta name=\"twitter:card\" content=\"summary_large_image\">"),
                "Should contain twitter:card with summary_large_image");
    }

    @Test
    void testRenderWithoutImageMetadata() {
        // Setup test data without image metadata
        Map<String, List<String>> pageData = new HashMap<>();
        pageData.put("title", List.of("Test Post Without Image"));

        Page page = Page.builder()
                .path("/2026/01/30/test-post.html")
                .data(pageData)
                .build();

        Author author = new Author();
        author.setName("Test Author");

        Site site = new Site();
        site.setTitle("Test Site");
        site.setDescription("Test Description");
        site.setBaseUrl("https://example.com");
        site.setAuthor(author);
        site.setTwitterUsername("testuser");

        Context context = new Context();
        context.setSite(site);

        RenderModel renderModel = new RenderModel();
        renderModel.setPage(page);
        renderModel.setContext(context);

        // Render SEO meta tags
        DomContent seoContent = SEO.render(renderModel);
        String html = seoContent.render();

        // Verify OpenGraph image metadata is not present
        assertFalse(html.contains("og:image:width"),
                "Should not contain og:image:width meta tag when no image metadata");
        assertFalse(html.contains("og:image:height"),
                "Should not contain og:image:height meta tag when no image metadata");
        assertFalse(html.contains("og:image:alt"),
                "Should not contain og:image:alt meta tag when no image metadata");
        assertFalse(html.contains("twitter:image:alt"),
                "Should not contain twitter:image:alt meta tag when no image metadata");
    }

    @Test
    void testSchemaOrgJsonLdWithAbsoluteImageUrl() {
        // Setup test data with image
        Map<String, List<String>> pageData = new HashMap<>();
        pageData.put("title", List.of("Test Post With Image"));
        pageData.put("image", List.of("/images/test.jpg"));
        pageData.put("layout", List.of("post"));
        pageData.put("date", List.of("2026-01-30"));
        pageData.put("tags", List.of("test", "example"));

        Page page = Page.builder()
                .path("/2026/01/30/test-post.html")
                .data(pageData)
                .build();

        Author author = new Author();
        author.setName("Test Author");

        Site site = new Site();
        site.setTitle("Test Site");
        site.setDescription("Test Description");
        site.setBaseUrl("https://example.com");
        site.setAuthor(author);
        site.setTwitterUsername("testuser");

        Context context = new Context();
        context.setSite(site);

        RenderModel renderModel = new RenderModel();
        renderModel.setPage(page);
        renderModel.setContext(context);

        // Render SEO meta tags
        DomContent seoContent = SEO.render(renderModel);
        String html = seoContent.render();

        // Verify Schema.org JSON-LD contains absolute image URL
        assertTrue(html.contains("\"image\" : \"https://example.com/images/test.jpg\""),
                "Schema.org JSON-LD should contain absolute image URL");
    }

    @Test
    void testSchemaOrgJsonLdIncludesDescriptionForWebSite() {
        // Setup test data for a non-blog page (website type)
        Map<String, List<String>> pageData = new HashMap<>();
        pageData.put("title", List.of("About Page"));
        pageData.put("description", List.of("This is a custom description for the about page"));

        Page page = Page.builder()
                .path("/about.html")
                .data(pageData)
                .build();

        Author author = new Author();
        author.setName("Test Author");

        Site site = new Site();
        site.setTitle("Test Site");
        site.setDescription("Default Site Description");
        site.setBaseUrl("https://example.com");
        site.setAuthor(author);
        site.setTwitterUsername("testuser");

        Context context = new Context();
        context.setSite(site);

        RenderModel renderModel = new RenderModel();
        renderModel.setPage(page);
        renderModel.setContext(context);

        // Render SEO meta tags
        DomContent seoContent = SEO.render(renderModel);
        String html = seoContent.render();

        // Verify Schema.org JSON-LD contains description for WebSite type
        assertTrue(html.contains("\"description\" : \"This is a custom description for the about page\""),
                "Schema.org JSON-LD should contain page description for WebSite type");
    }
}
