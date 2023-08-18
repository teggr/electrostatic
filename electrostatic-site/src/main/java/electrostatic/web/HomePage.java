package electrostatic.web;

import electrostatic.engine.Page;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.each;
import static j2html.TagCreator.h1;

public class HomePage {
    public static Page create() {
        return Page.builder()
                .path("index.html")
                .data(Map.of("layout", List.of("default")))
                .renderFunction(HomePage::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {
        return each(
                h1("electro for the win")
        );
    }

}
