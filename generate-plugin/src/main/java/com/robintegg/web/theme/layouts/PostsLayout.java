package com.robintegg.web.theme.layouts;

import com.robintegg.web.content.IndexContent;
import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.posts.PostsPlugin;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class PostsLayout {
  public static Layout create() {
    return Layout.builder()
        .data(Map.of("layout", List.of("default")))
        .renderFunction(PostsLayout::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {

    PagedContent<IndexContent> indexedContent = PostsPlugin.INSTANCE.getIndexedContent(renderModel.getPage().getPageable());

    return div()
        .withClass("home")
        .with(
            iff(
                renderModel.getPage().getTitle() != null,
                h1()
                    .withClass("page-heading")
                    .withText(renderModel.getPage().getTitle())
            ),
            renderModel.getContent(),
            iff(
                indexedContent.getContent().size() > 0,
                each(
                    iff(
                        renderModel.getPage().getListTitle() != null,
                        h2()
                            .withClass("post-list-heading")
                            .withText(renderModel.getPage().getListTitle())
                    ),
                    ul()
                        .withClass("post-list")
                        .with(
                            each(indexedContent.getContent(), indexContent -> {
                              return li()
                                  .with(
                                      span()
                                          .withClass("post-meta")
                                          .withText(Utils.format(indexContent.getDate())),
                                      h3()
                                          .with(
                                              a()
                                                  .withClass("post-link")
                                                  .withHref(Utils.relativeUrl(indexContent.getUrl()))
                                                  .withText(Utils.escape(indexContent.getTitle()))
                                          ),
                                      iff(
                                          renderModel.getContext().getSite().showExcerpts(),
                                          indexContent.getExcerpt(renderModel)
                                      ),
                                      iff(
                                          "reading-list".equals(indexContent.getCategory()),
                                          indexContent.getContent(renderModel)
                                      ),
                                      iff(
                                          indexContent.getTags().size() > 0,
                                          ul()
                                              .withClass("post-tags")
                                              .with(
                                                  each(indexContent.getTags(), tag -> {
                                                    return li()
                                                        .with(
                                                            a()
                                                                .withHref(Utils.relativeUrl("/tags/" + tag))
                                                                .withText(Utils.escape(tag))
                                                        );
                                                  })
                                              )
                                      )
                                  );
                            })
                        )
                )

            ),
            div()
                .withClass("pagination")
                .with(
                    iffElse(
                        indexedContent.isPrevious(),
                        a()
                            .withHref(Utils.relativeUrl(indexedContent.getPreviousPagePath()))
                            .withRel("previous")
                            .withClass("previous")
                            .withText("Previous"),
                        span()
                            .withClass("previous")
                            .withText("Previous")
                    ),
                    span()
                        .withClass("page_number")
                        .withText(String.format("Page: %s" /* of %s */, indexedContent.getPage(), indexedContent.getTotalPages())),
                    iffElse(
                        indexedContent.isNext(),
                        a()
                            .withHref(Utils.relativeUrl(indexedContent.getNextPagePath()))
                            .withRel("next")
                            .withClass("next")
                            .withText("Next"),
                        span()
                            .withClass("next")
                            .withText("Next")
                    )

                )


        );

  }

}
