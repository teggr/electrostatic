package electrostatic.theme.home.pages;

import electrostatic.content.podcast.Podcast;
import electrostatic.engine.Page;
import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class PodcastsPage {

  public static Page create() {

    return Page.builder()
        .path("/podcasts/index.html")
        .data(Map.of(
            "layout", List.of("default"),
            "title", List.of("Podcasts"),
            "permalink", List.of("/podcasts"),
            "list_title", List.of("Podcasts"),
                "include_menu", List.of("true")
        ))
        .renderFunction(PodcastsPage::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {
    return div()
        .withClass("home")
        .with(
            ul()
                .with(
                    iff(
                        renderModel.getContentModel().getContentOfType(Podcast.class).size() > 0,
                        each(
                            h2()
                                .withClass("post-list-heading")
                                .withText(renderModel.getPage().getListTitle()),
                            ul()
                                .withClass("post-list")
                                .with(
                                    each(renderModel.getContentModel().getContentOfType(Podcast.class), podcast -> {
                                      return li()
                                          .with(
                                              h3()
                                                  .with(
                                                      a()
                                                          .withClass("post-link")
                                                          .withHref(Utils.relativeUrl(podcast.getUrl()))
                                                          .withText(Utils.relativeUrl(podcast.getTitle()))
                                                  ),
                                              text(podcast.getSubtitle()),
                                              iff(
                                                  podcast.getTags().size() > 0,
                                                  ul()
                                                      .withClass("post-tags")
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
                        )

                    )
                )
        );
  }

}
