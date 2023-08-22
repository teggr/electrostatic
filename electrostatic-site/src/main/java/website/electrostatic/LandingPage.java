package website.electrostatic;

import electrostatic.engine.Page;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

@Slf4j
public class LandingPage {

    public static Page create() {
        return Page.builder()
                .path("index.html")
                .data(Map.of(
                        "layout", List.of("default"),
                        "title", List.of("electrostatic site generation")
                ))
                .renderFunction(LandingPage::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {
        return each(
                div()
                        .withClasses("p-5", "mb-4", "bg-body-tertiary", "rounded-3")
                        .with(
                                div()
                                        .withClasses("container-fluid", "py-5")
                                        .with(
                                                h1("electrostatic")
                                                        .withClasses("display-5", "fw-bold"),
                                                p("Generate web sites using Java.")
                                                        .withClasses("col-md-8", "fs-4"),
                                                a("Get started")
                                                        .withClasses("btn", "btn-primary", "btn-lg")
                                                        .withHref("#getting-started"),
                                                a("Read the docs")
                                                        .withClasses("btn", "btn-primary", "btn-lg")
                                                        .withHref(renderModel.linkTo("/docs"))
                                        )
                        ),
                div().withClass("container").with(
                        h1("Getting Started").withId("getting-started")
                )
        );
    }

}
