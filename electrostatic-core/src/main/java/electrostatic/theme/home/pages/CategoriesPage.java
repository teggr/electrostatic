package electrostatic.theme.home.pages;

import electrostatic.categories.CategoriesPlugin;
import electrostatic.engine.Page;
import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.TagCreator;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static electrostatic.utils.j2html.SvgTagGenerator.svg;
import static electrostatic.utils.j2html.SvgTagGenerator.use;
import static j2html.TagCreator.*;

public class CategoriesPage {

    public static Page create() {

        return Page.builder()
                .path("/categories/index.html")
                .data(Map.of(
                        "layout", List.of("default"),
                        "title", List.of("Categories"),
                        "permalink", List.of("/categories"),
                        "include_menu", List.of("true")
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
                        TagCreator.iff(
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
                                                                    TagCreator.each(CategoriesPlugin.INSTANCE.getPostsInCategory(category), post -> {
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
                                                                                        TagCreator.iff(
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
                                                        svg()
                                                                .withClasses("svg-icon", "orange")
                                                                .with(
                                                                        use()
                                                                                .attr("xlink:href", Utils.relativeUrl("/minima-social-icons.svg#rss"))
                                                                )
                                                )
                                )
                );

    }

}
