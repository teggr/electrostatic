package electrostatic.theme.layouts;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class BookLayout {

    public static Layout create() {
        return Layout.builder()
                .data(Map.of("layout", List.of("default")))
                .renderFunction(BookLayout::render)
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
                                    p(renderModel.getPage().getSubtitle()),
                                    img()
                                        .withWidth("150px")
                                        .withSrc(renderModel.getPage().getImageUrl())
                                ),
                        a()
                                .withClass("u-url")
                                .withHref(Utils.relativeUrl(renderModel.getPage().getUrl()))
                                .attr("hidden")
                );

    }

}
