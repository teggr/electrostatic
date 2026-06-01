package site.electrostatic.theme.pages;

import site.electrostatic.engine.Page;
import site.electrostatic.engine.Pageable;
import site.electrostatic.engine.RenderModel;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.each;

public class PostsPage {

    public static Page create(String path, Pageable pageable) {

        return Page.builder()
                .path(path)
                .pageable(pageable)
                .includeMenu(pageable.getPage() == 1)
                .data(Map.of(
                        "layout", List.of("posts"),
                        "title", List.of("Posts"),
                        "list_title", List.of("Posts")
                ))
                .renderFunction(PostsPage::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {
        return each();
    }

}
