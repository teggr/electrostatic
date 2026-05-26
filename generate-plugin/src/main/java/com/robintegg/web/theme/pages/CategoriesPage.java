package com.robintegg.web.theme.pages;

import com.robintegg.web.categories.CategoriesPlugin;
import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;
import static j2html.TagCreator.rawHtml;

public class CategoriesPage {

    public static Page create() {

        return Page.builder()
                .path("/categories/index.html")
                .includeMenu(true)
                .data(Map.of(
                        "layout", List.of("default"),
                        "title", List.of("Categories"),
                        "permalink", List.of("/categories")
                ))
                .renderFunction(CategoriesPage::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {

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
                    CategoriesPlugin.INSTANCE.getCategories().size() > 0,
                    each(CategoriesPlugin.INSTANCE.getCategories(), category -> {
                            return each(
                                h2()
                                    .withClass("post-list-heading")
                                    .with(a()
                                        .withText(Utils.capitalize(category))
                                        .withHref(Utils.relativeUrl("/categories/" + category))
                                    )
                                ,
                                ul()
                                    .withClass("post-list")
                                    .with(
                                        each(CategoriesPlugin.INSTANCE.getPostsInCategory(category), post -> {
                                            return li()
                                                .with(
                                                    span()
                                                        .withClass("post-meta")
                                                        .withText(Utils.format(post.getDate())),
                                                    h3()
                                                        .with(
                                                            a()
                                                                .withClass("post-link")
                                                                .withHref(Utils.relativeUrl(post.getUrl()))
                                                                .withText(Utils.escape(post.getTitle()))
                                                        ),
                                                    iff(
                                                        renderModel.getContext().getSite().showExcerpts(),
                                                        post.getExcerpt(renderModel)
                                                    )
                                                );
                                        })
                                    )
                            );
                        }
                    )
                ),
                p()
                    .withClass("feed-subscribe")
                    .with(
                        a()
                            .withHref(Utils.relativeUrl("/feed.xml"))
                            .with(
                                rawHtml(
                                    String.format("<svg class=\"svg-icon orange\"><use xlink:href=\"%s\"></use></svg><span>Subscribe</span>", Utils.relativeUrl("/minima-social-icons.svg#rss"))
                                )
                            )
                    )
            );

    }

}
