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

class FeedTest {

    @Test
    void getContent() {

        Feed feed = new Feed();

        ContentModel contentModel = new ContentModel();
        Site site = new Site();
        Author author = new Author();
        author.setName("Robin Tegg");
        site.setAuthor(author);
        site.setTitle("my title");
        site.setDescription("description");
        site.setBaseUrl("rt.com");
        site.setUrl("someurl");
        contentModel.setSite(site);

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
            public DomContent getExcerpt(RenderContentModel contentModel) {
                return TagCreator.rawHtml("<div class='c'>exceprpt</div>");
            }

            @Override
            public String getCategory() {
                return "a-gategory";
            }

            @Override
            public DomContent getContent(RenderContentModel contentModel) {
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
                return "image.png";
            }
        });

        System.out.println(feed.getContent(contentModel));

    }
}