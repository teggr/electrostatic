package electrostatic.website;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;
import lombok.extern.slf4j.Slf4j;

import static electrostatic.utils.bootstrap.j2html.BootstrapTagGenerator.*;
import static electrostatic.utils.googlefonts.j2html.GoogleFontsTagGenerator.*;
import static electrostatic.utils.j2html.MermaidJsTagCreator.importAndInitializeMermaidJs;
import static electrostatic.utils.prism.j2html.PrismTagGenerator.*;
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
                                        charset(),
                                        viewPort(),
                                        title(renderModel.getPage().getTitle()),
                                        fontsGoogleApis(),
                                        fontsGStatic(),
                                        fontStylesheet("Audiowide"),
                                        iconsStylesheet(),
                                        minStylesheet(),
                                        tomorrowStylesheet(),
                                        style(rawHtml("""
                                                .jumbo-title {
                                                    font-family: 'Audiowide', cursive;
                                                }
                                                """))
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
                                                                                                span("electrostatic").withClass("jumbo-title")
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
                                                iffElse(!renderModel.getPage().getUrl().equals("index.html"),
                                                        div().withClasses("container", "mt-5").with(
                                                                renderModel.getContent()
                                                        ),
                                                        renderModel.getContent()
                                                ),
                                                siteFooter(renderModel),
                                                minBundle(),
                                                minCore(),
                                                minAutoloader(),
                                                importAndInitializeMermaidJs()
                                        )
                        )
        );
    }

    private static DomContent siteFooter(RenderModel renderModel) {
        return div()
                .withClass("container")
                .with(
                        footer()
                                .withClasses("d-flex", "flex-wrap", "justify-content-between", "align-items-center", "py-3", "my-4", "border-top")
                                .with(
                                        p("© 2023 r3l Software")
                                                .withClasses("col-md-4", "mb-0", "text-body-secondary"),
                                        a()
                                                .withHref("/")
                                                .withClasses("col-md-4", "d-flex", "align-items-center", "justify-content-center", "mb-3", "mb-md-0", "me-md-auto", "link-body-emphasis", "text-decoration-none")
                                                .with(
                                                        i().withClasses("bi", "bi-lightning")
                                                ),
                                        ul()
                                                .withClasses("nav", "col-md-4", "justify-content-end")
                                                .with(
                                                        li()
                                                                .withClass("nav-item")
                                                                .with(
                                                                        a("Home")
                                                                                .withHref(renderModel.linkTo("/"))
                                                                                .withClasses("nav-link", "px-2", "text-body-secondary")
                                                                ),
                                                        li()
                                                                .withClass("nav-item")
                                                                .with(
                                                                        a("Docs")
                                                                                .withHref(renderModel.linkTo("/docs"))
                                                                                .withClasses("nav-link", "px-2", "text-body-secondary")
                                                                ),
                                                        li()
                                                                .withClass("nav-item")
                                                                .with(
                                                                        a("Attributions")
                                                                                .withHref(renderModel.linkTo("/attributions.html"))
                                                                                .withClasses("nav-link", "px-2", "text-body-secondary")
                                                                )
                                                )
                                )
                );
    }

}
