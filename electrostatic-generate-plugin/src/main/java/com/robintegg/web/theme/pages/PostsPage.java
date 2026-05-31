package com.robintegg.web.theme.pages;

import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.Pageable;
import com.robintegg.web.engine.RenderModel;
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
