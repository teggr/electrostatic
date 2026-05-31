package com.robintegg.web.theme.pages;

import com.robintegg.web.content.podcast.Podcast;
import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class PodcastsPage {

  public static Page create() {

    return Page.builder()
        .path("/podcasts/index.html")
        .includeMenu(true)
        .data(Map.of(
            "layout", List.of("default"),
            "title", List.of("Podcasts"),
            "permalink", List.of("/podcasts"),
            "list_title", List.of("Podcasts")
        ))
        .renderFunction(PodcastsPage::render)
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
                    text("You can also view this list on "),
                    a()
                        .withHref("https://lists.pocketcasts.com/60ff4a25-0374-40f6-906d-92a8ea884b10")
                        .withTarget("_blank")
                        .withRel("noopener noreferrer")
                        .withText("Pocket Casts"),
                    text(".")
                ),
            div()
                .withClass("podcast-search")
                .withStyle("margin-bottom: 20px;")
                .with(
                    input()
                        .withType("text")
                        .withId("podcast-search-input")
                        .withPlaceholder("Search podcasts...")
                        .withStyle("width: 100%; padding: 10px; font-size: 16px; border: 1px solid #ccc; border-radius: 4px;")
                ),
            iff(
                renderModel.getContentModel().getContentOfType(Podcast.class).size() > 0,
                div()
                    .withId("podcasts-container")
                    .withClass("podcasts-grid")
                    .withStyle("display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;")
                    .with(
                        each(renderModel.getContentModel().getContentOfType(Podcast.class).stream()
                            .sorted(Comparator.comparing(Podcast::getTitle))
                            .toList(), podcast -> {
                          return div()
                              .withClass("podcast-card")
                              .attr("data-title", podcast.getTitle().toLowerCase())
                              .attr("data-subtitle", podcast.getSubtitle().toLowerCase())
                              .attr("data-tags", String.join(" ", podcast.getTags()).toLowerCase())
                              .withStyle("border: 1px solid #ddd; padding: 15px; border-radius: 8px; background: white;")
                              .with(
                                  iff(
                                      podcast.getIcon() != null,
                                      div()
                                          .withStyle("width: 100%; height: 150px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; border-radius: 4px; margin-bottom: 10px; overflow: hidden;")
                                          .with(
                                              img()
                                                  .withSrc(podcast.getIcon())
                                                  .withAlt(podcast.getTitle())
                                                  .withStyle("max-width: 100%; max-height: 100%; width: auto; height: auto; object-fit: contain;")
                                          )
                                  ),
                                  h3()
                                      .withStyle("margin-top: 0; margin-bottom: 10px;")
                                      .with(
                                          a()
                                              .withClass("post-link")
                                              .withHref(Utils.relativeUrl(podcast.getUrl()))
                                              .withText(podcast.getTitle())
                                      ),
                                  p()
                                      .withStyle("font-size: 14px; color: #666; line-height: 1.5;")
                                      .withText(podcast.getSubtitle()),
                                  div()
                                      .withStyle("margin-top: 10px;")
                                      .with(
                                          iff(
                                              podcast.getWebsite() != null,
                                              a()
                                                  .withHref(podcast.getWebsite())
                                                  .withTarget("_blank")
                                                  .withRel("noopener noreferrer")
                                                  .withStyle("margin-right: 10px; color: #0066cc;")
                                                  .withText("Website")
                                          ),
                                          iff(
                                              podcast.getRssUrl() != null,
                                              a()
                                                  .withHref(podcast.getRssUrl())
                                                  .withTarget("_blank")
                                                  .withRel("noopener noreferrer")
                                                  .withStyle("margin-right: 10px; color: #ff6600;")
                                                  .withText("RSS")
                                          ),
                                          iff(
                                              podcast.getPocketcastsUrl() != null,
                                              a()
                                                  .withHref(podcast.getPocketcastsUrl())
                                                  .withTarget("_blank")
                                                  .withRel("noopener noreferrer")
                                                  .withStyle("color: #f43e37;")
                                                  .withText("Pocket Casts")
                                          )
                                      ),
                                  iff(
                                      podcast.getTags().size() > 0,
                                      ul()
                                          .withClass("post-tags")
                                          .withStyle("margin-top: 10px;")
                                          .with(
                                              each(podcast.getTags(), tag -> {
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
                      const searchInput = document.getElementById('podcast-search-input');
                      const podcastCards = document.querySelectorAll('.podcast-card');
                      
                      searchInput.addEventListener('input', function() {
                        const searchTerm = this.value.toLowerCase();
                        
                        podcastCards.forEach(function(card) {
                          const title = card.getAttribute('data-title');
                          const subtitle = card.getAttribute('data-subtitle');
                          const tags = card.getAttribute('data-tags');
                          
                          if (title.includes(searchTerm) || 
                              subtitle.includes(searchTerm) || 
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
