package com.robintegg.web.content.post;

import com.robintegg.web.engine.Context;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.site.Author;
import com.robintegg.web.site.Site;
import j2html.tags.DomContent;
import org.commonmark.Extension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PostHeadingAnchorTest {

    @Test
    void getContent_shouldAddAnchorIdToHeadings() {
        // Given
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Test Post"));
        data.put("date", List.of("2024-01-01"));

        List<Extension> extensions = List.of(HeadingAnchorExtension.create());
        Parser parser = Parser.builder()
            .extensions(extensions)
            .build();
        
        Node document = parser.parse("## Java FX\n\nSome content about JavaFX.\n\n## Spring Boot\n\nSome content about Spring Boot.");

        Post post = new Post("2024-01-01-test-post", data, document);

        RenderModel renderModel = createRenderModel();

        // When
        DomContent content = post.getContent(renderModel);

        // Then
        String html = content.render();
        
        // Verify heading IDs are present and lowercase with hyphens
        assertTrue(html.contains("id=\"java-fx\""), "Should have 'java-fx' id");
        assertTrue(html.contains("id=\"spring-boot\""), "Should have 'spring-boot' id");
        
        // Verify the heading structure
        assertTrue(html.contains("<h2 id=\"java-fx\">Java FX</h2>"), "Should have proper h2 structure with id");
        assertTrue(html.contains("<h2 id=\"spring-boot\">Spring Boot</h2>"), "Should have proper h2 structure with id");
    }

    @Test
    void getContent_shouldHandleSpecialCharactersInHeadings() {
        // Given
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Test Post"));
        data.put("date", List.of("2024-01-01"));

        List<Extension> extensions = List.of(HeadingAnchorExtension.create());
        Parser parser = Parser.builder()
            .extensions(extensions)
            .build();
        
        Node document = parser.parse("## Testing & Debugging\n\nContent.\n\n## What's New?\n\nMore content.");

        Post post = new Post("2024-01-01-test-post", data, document);

        RenderModel renderModel = createRenderModel();

        // When
        DomContent content = post.getContent(renderModel);

        // Then
        String html = content.render();
        
        // Verify special characters are handled
        assertTrue(html.contains("id=\"testing--debugging\""), "Should convert & to - in id");
        assertTrue(html.contains("id=\"whats-new\""), "Should handle apostrophes and question marks");
    }

    @Test
    void getContent_shouldHandleMultipleLevelHeadings() {
        // Given
        Map<String, List<String>> data = new HashMap<>();
        data.put("title", List.of("Test Post"));
        data.put("date", List.of("2024-01-01"));

        List<Extension> extensions = List.of(HeadingAnchorExtension.create());
        Parser parser = Parser.builder()
            .extensions(extensions)
            .build();
        
        Node document = parser.parse("# Main Title\n\n## Subtitle One\n\n### Sub-subtitle\n\n## Subtitle Two");

        Post post = new Post("2024-01-01-test-post", data, document);

        RenderModel renderModel = createRenderModel();

        // When
        DomContent content = post.getContent(renderModel);

        // Then
        String html = content.render();
        
        // Verify all heading levels have IDs
        assertTrue(html.contains("id=\"main-title\""), "h1 should have id");
        assertTrue(html.contains("id=\"subtitle-one\""), "h2 should have id");
        assertTrue(html.contains("id=\"sub-subtitle\""), "h3 should have id");
        assertTrue(html.contains("id=\"subtitle-two\""), "h2 should have id");
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
