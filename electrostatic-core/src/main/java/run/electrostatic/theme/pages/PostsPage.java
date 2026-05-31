package run.electrostatic.theme.pages;

import run.electrostatic.engine.Page;
import run.electrostatic.engine.Pageable;
import run.electrostatic.engine.RenderModel;
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
