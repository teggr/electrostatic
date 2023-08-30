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
                                                .attr("rel", "preconnect")
                                                .withHref("https://fonts.googleapis.com"),
                                        link()
                                                .attr("rel", "preconnect")
                                                .withHref("https://fonts.gstatic.com")
                                                .attr("crossorigin"),
                                        /* https://fonts.google.com/specimen/Audiowide?preview.text=electrostatic&preview.text_type=custo */
                                        link()
                                                .withHref("https://fonts.googleapis.com/css2?family=Audiowide&display=swap")
                                                .attr("rel", "stylesheet"),
                                        link()
                                                .attr("rel", "stylesheet")
                                                .withHref("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"),
                                        link()
                                                .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css")
                                                .attr("rel", "stylesheet")
                                                .attr("integrity", "sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9")
                                                .attr("crossorigin", "anonymous"),
                                        link()
                                                .attr("rel", "stylesheet")
                                                .withHref("https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css")
                                                .attr("integrity", "sha512-vswe+cgvic/XBoF1OcM/TeJ2FW0OofqAVdCZiEYkd6dwGXthvkSFWOoGGJgS2CW70VK5dQM5Oh+7ne47s74VTg==")
                                                .attr("crossorigin", "anonymous")
                                                .attr("referrerpolicy", "no-referrer"),
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
                                                iffElse(!renderModel.getPage().getUrl().equals("index.html"),
                                                        div().withClasses("container", "mt-5").with(
                                                                renderModel.getContent()
                                                        ),
                                                        renderModel.getContent()
                                                ),
                                                siteFooter(renderModel),
                                                script()
                                                        .attr("src", "https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js")
                                                        .attr("integrity", "sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm")
                                                        .attr("crossorigin", "anonymous"),
                                                script()
                                                        .attr("src", "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-core.min.js")
                                                        .attr("integrity", "sha512-9khQRAUBYEJDCDVP2yw3LRUQvjJ0Pjx0EShmaQjcHa6AXiOv6qHQu9lCAIR8O+/D8FtaCoJ2c0Tf9Xo7hYH01Q==")
                                                        .attr("crossorigin", "anonymous")
                                                        .attr("referrerpolicy", "no-referrer"),
                                                script()
                                                        .attr("src", "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/plugins/autoloader/prism-autoloader.min.js")
                                                        .attr("integrity", "sha512-SkmBfuA2hqjzEVpmnMt/LINrjop3GKWqsuLSSB3e7iBmYK7JuWw4ldmmxwD9mdm2IRTTi0OxSAfEGvgEi0i2Kw==")
                                                        .attr("crossorigin", "anonymous")
                                                        .attr("referrerpolicy", "no-referrer")
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
