package com.robintegg.web.engine;

import com.robintegg.web.content.IndexContent;
import com.robintegg.web.feed.Feed;
import com.robintegg.web.site.Author;
import com.robintegg.web.site.Site;
import j2html.TagCreator;
import j2html.tags.DomContent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedTest {

    @Test
    void getContent() {

        Feed feed = new Feed();

        RenderModel renderModel = new RenderModel();
        Context context = new Context();
        Site site = new Site();
        Author author = new Author();
        author.setName("Robin Tegg");
        site.setAuthor(author);
        site.setTitle("my title");
        site.setDescription("description");
        site.setBaseUrl("https://robintegg.com");
        context.setSite(site);
        renderModel.setContext(context);

        feed.addContent(new IndexContent() {
            @Override
            public LocalDate getDate() {
                return LocalDate.now();
            }

            @Override
            public String getUrl() {
                return "/content";
            }

            @Override
            public String getTitle() {
                return "content title";
            }

            @Override
            public DomContent getExcerpt(RenderModel renderModel) {
                return TagCreator.rawHtml("<div class='c'>exceprpt</div>");
            }

            @Override
            public String getCategory() {
                return "a-gategory";
            }

            @Override
            public DomContent getContent(RenderModel renderModel) {
                return TagCreator.rawHtml("<div class='c'>aome tezt</div>");
            }

            @Override
            public List<String> getTags() {
                return List.of("1","2");
            }

            @Override
            public String getAuthor() {
                return "just robin";
            }

            @Override
            public String getImage() {
                return "/images/image.png";
            }
        });

        String feedContent = feed.getContent(renderModel);
        System.out.println(feedContent);

        // Verify that the feed contains absolute URLs
        assertTrue(feedContent.contains("<link href=\"https://robintegg.com/content\""),
            "Feed entry link should use absolute URL");
        assertTrue(feedContent.contains("<id>https://robintegg.com/content</id>"),
            "Feed entry id should use absolute URL");
        assertTrue(feedContent.contains("url=\"https://robintegg.com/images/image.png\""),
            "Feed media URLs should use absolute URLs");

    }
}