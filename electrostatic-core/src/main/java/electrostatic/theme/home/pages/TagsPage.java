package electrostatic.theme.home.pages;

import electrostatic.engine.Page;
import electrostatic.engine.RenderModel;
import electrostatic.tags.TagPlugin;
import electrostatic.utils.Utils;
import j2html.TagCreator;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class TagsPage {

    public static Page create() {

        return Page.builder()
                .path("/tags/index.html")
                .includeMenu(true)
                .data(Map.of(
                        "layout", List.of("default"),
                        "title", List.of("Tags"),
                        "permalink", List.of("/tags")
                ))
                .renderFunction(TagsPage::render)
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
                        ul()
                                .withClass("post-list")
                                .with(
                                        TagCreator.each(TagPlugin.INSTANCE.getTags().stream().sorted(String.CASE_INSENSITIVE_ORDER).toList(), tag ->{
                                            return h3()
                                                    .with(
                                                            a()
                                                                    .withClass("post-link")
                                                                    .withHref(Utils.relativeUrl("/tags/" + tag))
                                                                    .withText(Utils.capitalize(tag))
                                                    );
                                        })
                                )
                );
    }


}
