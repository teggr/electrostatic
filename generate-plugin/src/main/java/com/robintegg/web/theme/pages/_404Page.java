package com.robintegg.web.theme.pages;

import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.RenderModel;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class _404Page {

  public static Page create() {

    return Page.builder()
        .path("404.html")
        .data(Map.of("layout", List.of("default")))
        .renderFunction(_404Page::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {
    return each(
        style()
            .withMedia("screen")
            .withType("text/css")
            .withText("""
                    .container {
                        margin: 10px auto;
                        max-width: 600px;
                        text-align: center;
                      }
                      h1 {
                        margin: 30px 0;
                        font-size: 4em;
                        line-height: 1;
                        letter-spacing: -1px;
                      }
                """),
        div()
            .withClass("container")
            .with(
                h1("404"),
                p()
                    .with(
                        strong("Page not found :(")
                    ),
                p("The requested page could not be found.")
            )
    );
  }

}
