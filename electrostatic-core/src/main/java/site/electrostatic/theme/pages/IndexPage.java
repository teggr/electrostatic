package site.electrostatic.theme.pages;

import site.electrostatic.engine.Page;
import site.electrostatic.engine.Pageable;
import site.electrostatic.engine.RenderModel;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.each;

public class IndexPage {

    public static Page create(String path, Pageable pageable) {

        return Page.builder()
                .path(path)
                .pageable(pageable)
                .data(Map.of(
                "layout", List.of("home")
                ))
                .renderFunction(IndexPage::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {
        return each();
    }

}
