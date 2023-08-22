package website.electrostatic;

import electrostatic.engine.Page;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;
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
                jumboIntroduction(renderModel),
                iconGrid(renderModel),
                div().withClass("container").with(
                        h1("Getting Started").withId("getting-started")
                )
        );
    }

    private static DomContent iconGrid(RenderModel renderModel) {
        return div()
                .withClasses("container", "px-4", "py-5")
                .withId("icon-grid")
                .with(
                        h2("Features")
                                .withClasses("pb-2", "border-bottom"),
                        div()
                                .withClasses("row", "row-cols-1", "row-cols-sm-2", "row-cols-md-3", "row-cols-lg-4", "g-4", "py-5")
                                .with(
                                        div()
                                                .withClasses("col", "d-flex", "align-items-start")
                                                .with(
                                                        i().withClasses("bi", "bi-bootstrap", "text-body-secondary", "flex-shrink-0", "me-3")
                                                                .attr("width", "1.75em")
                                                                .attr("height", "1.75em"),
                                                        div(
                                                                h3("100% Java")
                                                                        .withClasses("fw-bold", "mb-0", "fs-4", "text-body-emphasis"),
                                                                p("Paragraph of text beneath the heading to explain the heading.")
                                                        )
                                                ),
                                        div()
                                                .withClasses("col", "d-flex", "align-items-start")
                                                .with(
                                                        i().withClasses("bi", "bi-cpu-fill", "text-body-secondary", "flex-shrink-0", "me-3")
                                                                .attr("width", "1.75em")
                                                                .attr("height", "1.75em"),
                                                        div(
                                                                h3("Maven Plugin")
                                                                        .withClasses("fw-bold", "mb-0", "fs-4", "text-body-emphasis"),
                                                                p("Paragraph of text beneath the heading to explain the heading.")
                                                        )
                                                ),
                                        div()
                                                .withClasses("col", "d-flex", "align-items-start")
                                                .with(
                                                        i().withClasses("bi", "bi-calendar3", "text-body-secondary", "flex-shrink-0", "me-3")
                                                                .attr("width", "1.75em")
                                                                .attr("height", "1.75em"),
                                                        div(
                                                                h3("j2html")
                                                                        .withClasses("fw-bold", "mb-0", "fs-4", "text-body-emphasis"),
                                                                p("Paragraph of text beneath the heading to explain the heading.")
                                                        )
                                                ),
                                        div()
                                                .withClasses("col", "d-flex", "align-items-start")
                                                .with(
                                                        i().withClasses("bi", "bi-home", "text-body-secondary", "flex-shrink-0", "me-3")
                                                                .attr("width", "1.75em")
                                                                .attr("height", "1.75em"),
                                                        div(
                                                                h3("Plugins")
                                                                        .withClasses("fw-bold", "mb-0", "fs-4", "text-body-emphasis"),
                                                                p("Paragraph of text beneath the heading to explain the heading.")
                                                        )
                                                )
                                )
                );
    }

    private static DivTag jumboIntroduction(RenderModel renderModel) {
        return div()
                .withClasses("container", "col-xxl-8", "px-4", "py-5")
                .with(
                        div()
                                .withClasses("row", "flex-lg-row-reverse", "align-items-center", "g-5", "py-5")
                                .with(
                                        div()
                                                .withClasses("col-10", "col-sm-8", "col-lg-6")
                                                .with(
                                                        img()
                                                                .withSrc("https://img.freepik.com/free-vector/3d-wireframe-grid-room-background-vector_53876-168030.jpg")
                                                                .withClasses("d-block", "mx-lg-auto", "img-fluid")
                                                                .attr("alt", "Bootstrap Themes")
                                                                .attr("width", "700")
                                                                .attr("height", "500")
                                                                .attr("loading", "lazy")
                                                ),
                                        div()
                                                .withClass("col-lg-6")
                                                .with(
                                                        h1("electrostatic")
                                                                .withClasses("display-5", "fw-bold", "text-body-emphasis", "lh-1", "mb-3"),
                                                        p("Generate web sites using Java.")
                                                                .withClass("lead"),
                                                        div()
                                                                .withClasses("d-grid", "gap-2", "d-md-flex", "justify-content-md-start")
                                                                .with(
                                                                        a("Get started")
                                                                                .withHref("#getting-started")
                                                                                .withClasses("btn", "btn-primary", "btn-lg", "px-4", "me-md-2"),
                                                                        a("Read the docs")
                                                                                .withHref(renderModel.linkTo("/docs"))
                                                                                .withClasses("btn", "btn-outline-secondary", "btn-lg", "px-4")
                                                                )
                                                )
                                )
                );

    }

}
