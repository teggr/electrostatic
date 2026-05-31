package com.robintegg.web.theme.layouts;

import com.robintegg.web.categories.CategoriesPlugin;
import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class CategoryLayout {

    public static Layout create() {
        return Layout.builder()
                .data(Map.of("layout", List.of("default")))
                .renderFunction(CategoryLayout::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {

        return div()
                .withClass("home")
                .with(
                        h2()
                                .withClass("post-list-heading")
                                .withId(renderModel.getPage().getCategory())
                                .withText(Utils.capitalize(renderModel.getPage().getCategory())),
                        ul()
                                .withClass("post-list")
                                .with(
                                        each(CategoriesPlugin.INSTANCE.getPostsInCategory(renderModel.getPage().getCategory()), taggedContent -> {
                                            return each(
                                                    span()
                                                            .withClass("post-meta")
                                                            .withText(Utils.format(taggedContent.getDate())),
                                                    h3()
                                                            .with(
                                                                    a()
                                                                            .withClass("post-link")
                                                                            .withHref(Utils.relativeUrl(taggedContent.getUrl()))
                                                                            .withText(Utils.escape(taggedContent.getTitle()))
                                                            ),
                                                    iff(
                                                            renderModel.getContext().getSite().showExcerpts(),
                                                            taggedContent.getExcerpt(renderModel)
                                                    )
                                            );
                                        })
                                )
                );

    }

}
