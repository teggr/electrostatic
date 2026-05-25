package com.robintegg.web.theme.pages;

import com.robintegg.web.content.feed.FeedSubscription;
import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class FeedsPage {

  public static Page create() {

    return Page.builder()
        .path("/feeds/index.html")
        .includeMenu(true)
        .data(Map.of(
            "layout", List.of("default"),
            "title", List.of("Feeds"),
            "permalink", List.of("/feeds"),
            "list_title", List.of("My RSS Feed Subscriptions")
        ))
        .renderFunction(FeedsPage::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {
    return div()
        .withClass("home")
        .with(
            h2()
                .withClass("post-list-heading")
                .withText(renderModel.getPage().getListTitle()),
            p()
                .with(
                    text("RSS feeds remain one of the best ways to consume content on your own terms, without algorithmic manipulation. Below is a curated list of feeds I'm currently subscribed to.")
                ),
            div()
                .withClass("feed-search")
                .withStyle("margin-bottom: 20px;")
                .with(
                    input()
                        .withType("text")
                        .withId("feed-search-input")
                        .withPlaceholder("Search feeds...")
                        .withStyle("width: 100%; padding: 10px; font-size: 16px; border: 1px solid #ccc; border-radius: 4px;")
                ),
            iff(
                renderModel.getContentModel().getContentOfType(FeedSubscription.class).size() > 0,
                div()
                    .withId("feeds-container")
                    .withClass("feeds-grid")
                    .withStyle("display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;")
                    .with(
                        each(renderModel.getContentModel().getContentOfType(FeedSubscription.class).stream()
                            .sorted(Comparator.comparing(FeedSubscription::getTitle))
                            .toList(), feed -> {
                          return div()
                              .withClass("feed-card")
                              .attr("data-title", feed.getTitle().toLowerCase())
                              .attr("data-description", feed.getDescription().toLowerCase())
                              .attr("data-tags", String.join(" ", feed.getTags()).toLowerCase())
                              .withStyle("border: 1px solid #ddd; padding: 15px; border-radius: 8px; background: white;")
                              .with(
                                  iff(
                                      feed.getThumbnail() != null,
                                      div()
                                          .withStyle("width: 100%; height: 150px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; border-radius: 4px; margin-bottom: 10px; overflow: hidden;")
                                          .with(
                                              img()
                                                  .withSrc(feed.getThumbnail())
                                                  .withAlt(feed.getTitle())
                                                  .withStyle("max-width: 100%; max-height: 100%; width: auto; height: auto; object-fit: contain; image-rendering: -webkit-optimize-contrast; image-rendering: crisp-edges;")
                                          )
                                  ),
                                  h3()
                                      .withStyle("margin-top: 0; margin-bottom: 10px;")
                                      .withText(feed.getTitle()),
                                  p()
                                      .withStyle("font-size: 14px; color: #666; line-height: 1.5;")
                                      .withText(feed.getDescription()),
                                  div()
                                      .withStyle("margin-top: 10px;")
                                      .with(
                                          iff(
                                              feed.getSiteUrl() != null,
                                              a()
                                                  .withHref(feed.getSiteUrl())
                                                  .withTarget("_blank")
                                                  .withRel("noopener noreferrer")
                                                  .withStyle("margin-right: 10px; color: #0066cc;")
                                                  .withText("Website")
                                          ),
                                          iff(
                                              feed.getRssUrl() != null,
                                              a()
                                                  .withHref(feed.getRssUrl())
                                                  .withTarget("_blank")
                                                  .withRel("noopener noreferrer")
                                                  .withStyle("color: #ff6600;")
                                                  .withText("RSS Feed")
                                          )
                                      ),
                                  iff(
                                      feed.getTags().size() > 0,
                                      ul()
                                          .withClass("post-tags")
                                          .withStyle("margin-top: 10px;")
                                          .with(
                                              each(feed.getTags(), tag -> {
                                                return li()
                                                    .with(
                                                        a()
                                                            .withHref(Utils.relativeUrl("/tags/" + tag))
                                                            .withText(tag)
                                                    );
                                              })
                                          )
                                  )
                              );
                        })
                    )
            ),
            script()
                .withType("text/javascript")
                .with(rawHtml("""
                    document.addEventListener('DOMContentLoaded', function() {
                      const searchInput = document.getElementById('feed-search-input');
                      const feedCards = document.querySelectorAll('.feed-card');
                      
                      searchInput.addEventListener('input', function() {
                        const searchTerm = this.value.toLowerCase();
                        
                        feedCards.forEach(function(card) {
                          const title = card.getAttribute('data-title');
                          const description = card.getAttribute('data-description');
                          const tags = card.getAttribute('data-tags');
                          
                          if (title.includes(searchTerm) || 
                              description.includes(searchTerm) || 
                              tags.includes(searchTerm)) {
                            card.style.display = 'block';
                          } else {
                            card.style.display = 'none';
                          }
                        });
                      });
                    });
                    """))
        );
  }

}
