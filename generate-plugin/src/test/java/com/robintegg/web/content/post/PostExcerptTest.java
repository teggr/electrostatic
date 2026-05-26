package com.robintegg.web.content.post;

import com.robintegg.web.engine.Context;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.site.Author;
import com.robintegg.web.site.Site;
import j2html.tags.DomContent;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PostExcerptTest {

    @Test
    void getExcerpt_shouldUseDescriptionWhenAvailable() {
        // Given
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Test Post"));
        data.put("date", List.of("2024-01-01"));
        data.put("description", List.of("This is a description"));

        Parser parser = Parser.builder().build();
        Node document = parser.parse("# Title\n\nFirst paragraph.\n\nSecond paragraph.");

        Post post = new Post("2024-01-01-test-post", data, document);

        RenderModel renderModel = createRenderModel();

        // When
        DomContent excerpt = post.getExcerpt(renderModel);

        // Then
        String html = excerpt.render();
        assertEquals("This is a description", html);
    }

    @Test
    void getExcerpt_shouldUseFirstParagraphWhenNoDescription() {
        // Given
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Test Post"));
        data.put("date", List.of("2024-01-01"));

        Parser parser = Parser.builder().build();
        Node document = parser.parse("First paragraph with some text.\n\nSecond paragraph.\n\nThird paragraph.");

        Post post = new Post("2024-01-01-test-post", data, document);

        RenderModel renderModel = createRenderModel();

        // When
        DomContent excerpt = post.getExcerpt(renderModel);

        // Then
        String html = excerpt.render();
        assertTrue(html.contains("First paragraph"));
        assertFalse(html.contains("Second paragraph"));
        assertFalse(html.contains("Third paragraph"));
    }

    @Test
    void getExcerpt_shouldReturnEmptyWhenNoParagraph() {
        // Given
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Test Post"));
        data.put("date", List.of("2024-01-01"));

        Parser parser = Parser.builder().build();
        Node document = parser.parse("# Just a heading");

        Post post = new Post("2024-01-01-test-post", data, document);

        RenderModel renderModel = createRenderModel();

        // When
        DomContent excerpt = post.getExcerpt(renderModel);

        // Then
        String html = excerpt.render();
        assertEquals("", html);
    }

    @Test
    void getExcerpt_shouldNotIncludeFullContent() {
        // Given
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Test Post"));
        data.put("date", List.of("2024-01-01"));

        Parser parser = Parser.builder().build();
        String fullContent = "First paragraph.\n\nSecond paragraph.\n\nThird paragraph.\n\nFourth paragraph.";
        Node document = parser.parse(fullContent);

        Post post = new Post("2024-01-01-test-post", data, document);

        RenderModel renderModel = createRenderModel();

        // When
        DomContent excerpt = post.getExcerpt(renderModel);
        DomContent content = post.getContent(renderModel);

        // Then
        String excerptHtml = excerpt.render();
        String contentHtml = content.render();
        
        // Excerpt should be shorter than full content
        assertTrue(excerptHtml.length() < contentHtml.length());
        // Excerpt should contain first paragraph
        assertTrue(excerptHtml.contains("First paragraph"));
        // Content should contain all paragraphs
        assertTrue(contentHtml.contains("Fourth paragraph"));
    }

    private RenderModel createRenderModel() {
        RenderModel renderModel = new RenderModel();
        Context context = new Context();
        Site site = new Site();
        site.setBaseUrl("http://example.com");
        Author author = new Author();
        author.setName("Test Author");
        site.setAuthor(author);
        context.setSite(site);
        renderModel.setContext(context);
        return renderModel;
    }
}
