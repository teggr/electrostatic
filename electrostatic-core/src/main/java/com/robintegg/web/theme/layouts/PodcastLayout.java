package com.robintegg.web.theme.layouts;

import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.theme.includes.ShareButtons;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class PodcastLayout {

    public static Layout create() {
        return Layout.builder()
                .data(Map.of("layout", List.of("default")))
                .renderFunction(PodcastLayout::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {

        return article()
                .withClass("post h-entry")
                .attr("itemscope")
                .attr("itemtype", "http://schema.org/BlogPosting")
                .with(
                        header()
                                .withClass("post-header")
                                .with(
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
                                                                text("~")
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
                                                                renderModel.getPage().getAuthor() != null,
                                                                each(
                                                                        text(" • "),
                                                                        each(renderModel.getPage().getAuthor(), author -> {
                                                                            return span()
                                                                                    .attr("itemprop", "author")
                                                                                    .attr("itemscope")
                                                                                    .attr("itemtype", "http://schema.org/Person")
                                                                                    .with(
                                                                                            span()
                                                                                                    .withClass("p-author h-card")
                                                                                                    .attr("itemprop", "name")
                                                                                                    .withText(author)
                                                                                    );
                                                                        })
                                                                )

                                                        )
                                                )

                                ),
                        div()
                                .withClass("post-content e-content")
                                .attr("itemprop", "articleBody")
                                .with(
                                        div()
                                                .withClass("podcast-info")
                                                .withStyle("display: flex; align-items: center; gap: 10px;")
                                                .with(
                                                        iff(
                                                                renderModel.getPage().getIcon() != null,
                                                                img()
                                                                        .withSrc(renderModel.getPage().getIcon())
                                                                        .withAlt("Podcast Icon")
                                                                        .withClass("podcast-icon")
                                                                        .withWidth("64")
                                                                        .withHeight("64")
                                                        ),
                                                        text(
                                                                renderModel.getPage().getSubtitle()
                                                        )
                                                ),
                                        iff(
                                                renderModel.getPage().getWebsite() != null,
                                                div()
                                                        .withClass("podcast-website")
                                                        .with(
                                                                text("Website: "),
                                                                a()
                                                                        .withHref(renderModel.getPage().getWebsite())
                                                                        .withTarget("_blank")
                                                                        .withRel("noopener noreferrer")
                                                                        .withText(renderModel.getPage().getWebsite())
                                                        )
                                        ),
                                        div()
                                                .withClass("podcast-subscribe")
                                                .with(
                                                        text("Subscribe: "),
                                                        iff(
                                                                renderModel.getPage().getItunesUrl() != null,
                                                                a()
                                                                        .withHref(renderModel.getPage().getItunesUrl())
                                                                        .withTarget("_blank")
                                                                        .withRel("noopener noreferrer")
                                                                        .withText("iTunes")
                                                        ),
                                                        iff(
                                                                renderModel.getPage().getRssUrl() != null,
                                                                each(
                                                                        iff(
                                                                                renderModel.getPage().getRssUrl() != null,
                                                                                text(" | ")
                                                                        ),
                                                                        a()
                                                                                .withHref(renderModel.getPage().getRssUrl())
                                                                                .withTarget("_blank")
                                                                                .withRel("noopener noreferrer")
                                                                                .withText("RSS")
                                                                )
                                                        ),
                                                        iff(
                                                               
                                                                renderModel.getPage().getPocketcastsUrl() != null,
                                                                 each(
                                                                        iff(
                                                                                renderModel.getPage().getItunesUrl() != null || renderModel.getPage().getRssUrl() != null,
                                                                                text(" | ")
                                                                        ),
                                                                        a()
                                                                                .withHref(renderModel.getPage().getPocketcastsUrl())
                                                                                .withTarget("_blank")
                                                                                .withRel("noopener noreferrer")
                                                                                .withText("Pocket Casts")
                                                                )
                                                        )
                                                )
                                ),
                        ShareButtons.create(renderModel),
                        a()
                                .withClass("u-url")
                                .withHref(Utils.relativeUrl(renderModel.getPage().getUrl()))
                                .attr("hidden")
                );

    }

}
