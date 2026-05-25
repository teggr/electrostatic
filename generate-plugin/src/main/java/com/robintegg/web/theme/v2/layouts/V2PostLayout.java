package com.robintegg.web.theme.v2.layouts;

import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.theme.includes.ShareButtons;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class V2PostLayout {

  public static Layout create() {
    return Layout.builder()
        .data(Map.of("layout", List.of("default")))
        .renderFunction(V2PostLayout::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {

    String imageUrl = renderModel.getPage().getImageUrl();

    // Get tags from page data for category label and tag list
    List<String> pageTags = renderModel.getPage().getData().getOrDefault("tags", List.of());
    String categoryLabel = !pageTags.isEmpty() ? pageTags.get(0) : null;

    return article()
        .withClass("post h-entry")
        .attr("itemscope")
        .attr("itemtype", "http://schema.org/BlogPosting")
        .with(
            // Hero image — full width, before header, at top
            iff(
                imageUrl != null && !imageUrl.isEmpty(),
                div()
                    .withClass("post-hero-image")
                    .with(
                        img()
                            .withSrc(Utils.relativeUrl(imageUrl))
                            .withAlt(Utils.escape(renderModel.getPage().getTitle()))
                            .attr("itemprop", "image")
                    )
            ),
            // Post header: category label → title → meta
            header()
                .withClass("post-header")
                .with(
                    // Category / first tag label
                    iff(
                        categoryLabel != null,
                        span()
                            .withClass("post-category-label")
                            .withText(Utils.escape(categoryLabel))
                    ),
                    h1()
                        .withClass("post-title p-name")
                        .attr("itemprop", "name headline")
                        .withText(Utils.escape(renderModel.getPage().getTitle())),
                    p()
                        .withClass("post-meta")
                        .with(
                            time()
                                .withClass("dt-published")
                                .withDatetime(Utils.formatXmlSchema(renderModel.getPage().getDate()))
                                .attr("itemprop", "datePublished")
                                .withText(Utils.format(renderModel.getPage().getDate())),
                            iff(
                                renderModel.getPage().getModifiedDate() != null,
                                text(" · Updated ")
                            ),
                            iff(
                                renderModel.getPage().getModifiedDate() != null,
                                time()
                                    .withClass("dt-modified")
                                    .withDatetime(Utils.formatXmlSchema(renderModel.getPage().getModifiedDate()))
                                    .attr("itemprop", "dateModified")
                                    .withText(Utils.format(renderModel.getPage().getModifiedDate()))
                            ),
                            iff(
                                renderModel.getPage().getAuthor() != null && !renderModel.getPage().getAuthor().isEmpty(),
                                each(renderModel.getPage().getAuthor(), author ->
                                    span()
                                        .attr("itemprop", "author")
                                        .attr("itemscope")
                                        .attr("itemtype", "http://schema.org/Person")
                                        .with(
                                            text(" · "),
                                            span()
                                                .withClass("p-author h-card")
                                                .attr("itemprop", "name")
                                                .withText(author)
                                        )
                                )
                            )
                        )
                ),
            // Post body
            div()
                .withClass("post-content e-content")
                .attr("itemprop", "articleBody")
                .with(renderModel.getContent()),
            // Tags at bottom
            iff(
                !pageTags.isEmpty(),
                ul()
                    .withClass("post-tags")
                    .with(
                        each(pageTags, tag ->
                            li().with(
                                a()
                                    .withHref(Utils.relativeUrl("/tags/" + tag))
                                    .withText(Utils.escape(tag))
                            )
                        )
                    )
            ),
            ShareButtons.create(renderModel),
            a()
                .withClass("u-url")
                .withHref(renderModel.getPage().getUrl())
                .attr("hidden")
        );

  }

}
