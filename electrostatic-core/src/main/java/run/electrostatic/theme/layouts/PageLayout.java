package run.electrostatic.theme.layouts;

import run.electrostatic.engine.Layout;
import run.electrostatic.engine.RenderModel;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class PageLayout {

  public static Layout create() {
    return Layout.builder()
        .data(Map.of("layout", List.of("default")))
        .renderFunction(PageLayout::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {

    return article()
        .withClass("post")
        .with(
            header()
                .withClass("post-header")
                .with(
                    h1()
                        .withClass("post-title")
                        .withText( renderModel.getPage().getTitle() != null ? 
                            renderModel.getPage().getTitle() : "" ),
                    div()
                        .withClass("post-content")
                        .with(
                            renderModel.getContent()
                        )
                )
        );

  }

}
