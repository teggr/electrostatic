package website.electrostatic;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;
import lombok.extern.slf4j.Slf4j;

import static j2html.TagCreator.*;

@Slf4j
public class ElectroStaticLayout {

    public static Layout create() {
        return Layout.builder()
                .renderFunction(ElectroStaticLayout::render)
                .build();
    }

    private static DomContent render(RenderModel renderModel) {
        log.info("rendering layout");
        return join(
                document(),
                html()
                        .attr("lang", "en")
                        .with(
                                head(
                                        meta()
                                                .attr("charset", "utf-8"),
                                        meta()
                                                .withName("viewport")
                                                .attr("content", "width=device-width, initial-scale=1"),
                                        title(renderModel.getPage().getTitle()),
                                        link()
                                                .attr("rel", "stylesheet")
                                                .withHref("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"),
                                        link()
                                                .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css")
                                                .attr("rel", "stylesheet")
                                                .attr("integrity", "sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9")
                                                .attr("crossorigin", "anonymous")
                                ),
                                body().withClass("bg-dark")
                                        .attr("data-bs-theme", "dark")
                                        .with(
                                                nav()
                                                        .withClasses("navbar", "navbar-expand-lg", "border-bottom", "border-body")

                                                        .with(
                                                                div()
                                                                        .withClass("container-fluid")
                                                                        .with(
                                                                                a()
                                                                                        .withClass("navbar-brand")
                                                                                        .withHref(renderModel.linkTo("/"))
                                                                                        .with(
                                                                                                i().withClasses("bi", "bi-lightning-fill"),
                                                                                                text("electrostatic")
                                                                                        ),
                                                                                button()
                                                                                        .withClass("navbar-toggler")
                                                                                        .withType("button")
                                                                                        .attr("data-bs-toggle", "collapse")
                                                                                        .attr("data-bs-target", "#navbarSupportedContent")
                                                                                        .attr("aria-controls", "navbarSupportedContent")
                                                                                        .attr("aria-expanded", "false")
                                                                                        .attr("aria-label", "Toggle navigation")
                                                                                        .with(
                                                                                                span()
                                                                                                        .withClass("navbar-toggler-icon")
                                                                                        ),
                                                                                div()
                                                                                        .withClasses("collapse", "navbar-collapse")
                                                                                        .withId("navbarSupportedContent")
                                                                                        .with(
                                                                                                ul()
                                                                                                        .withClasses("navbar-nav", "me-auto", "mb-2", "mb-lg-0")
                                                                                                        .with(
                                                                                                                li()
                                                                                                                        .withClass("nav-item")
                                                                                                                        .with(
                                                                                                                                a("home")
                                                                                                                                        .withClasses("nav-link", "active")
                                                                                                                                        .attr("aria-current", "page")
                                                                                                                                        .withHref(renderModel.linkTo("/"))
                                                                                                                        ),
                                                                                                                li()
                                                                                                                        .withClass("nav-item")
                                                                                                                        .with(
                                                                                                                                a("docs")
                                                                                                                                        .withClass("nav-link")
                                                                                                                                        .withHref(renderModel.linkTo("/docs"))
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        ),
                                                iffElse(renderModel.getPage().getUrl().contains("/docs"),
                                                        div().withClasses("container", "mt-5").with(
                                                                renderModel.getContent()
                                                        ),
                                                        renderModel.getContent()
                                                ),
                                                script()
                                                        .attr("src", "https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js")
                                                        .attr("integrity", "sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm")
                                                        .attr("crossorigin", "anonymous")
                                        )
                        )
        );
    }

}
