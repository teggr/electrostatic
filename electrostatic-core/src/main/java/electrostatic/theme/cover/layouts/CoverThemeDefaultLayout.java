package electrostatic.theme.cover.layouts;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;
import j2html.tags.specialized.HeadTag;
import lombok.extern.slf4j.Slf4j;

import static electrostatic.utils.bootstrap.j2html.BootstrapTagGenerator.*;
import static electrostatic.utils.googlefonts.j2html.GoogleFontsTagGenerator.*;
import static j2html.TagCreator.*;

@Slf4j
public class CoverThemeDefaultLayout {

    public static Layout create() {
        return Layout.builder()
                .renderFunction(CoverThemeDefaultLayout::render)
                .build();
    }

    private static DomContent render(RenderModel renderModel) {
        log.info("rendering layout");
        return join(
                document(),
                html()
                        .attr("lang", "en")
                        .withClass("h-100")
                        .attr("data-bs-theme", "light")
                        .with(
                                layoutHead(renderModel),
                                renderModel.getContent()
                        )

        );
    }

    private static HeadTag layoutHead(RenderModel renderModel) {
        return head(
                charset(),
                viewPort(),
                meta()
                        .withName("description")
                        .attr("content", ""),
                meta()
                        .withName("author")
                        .attr("content", "Mark Otto, Jacob Thornton, and Bootstrap contributors"),
                meta()
                        .withName("generator")
                        .attr("content", "Hugo 0.115.4"),
                title(renderModel.getPage().getTitle()),
                link()
                        .attr("rel", "canonical")
                        .withHref("https://getbootstrap.com/docs/5.3/examples/cover/"),
                link()
                        .attr("rel", "stylesheet")
                        .withHref("https://cdn.jsdelivr.net/npm/@docsearch/css@3"),
                minStylesheet(),
                fontsGoogleApis(),
                fontsGStatic(),
                fontStylesheet("Special Elite"),
//                                        comment()
//                                                .attr("#comment", " Favicons "),
                link()
                        .attr("rel", "apple-touch-icon")
                        .withHref("/docs/5.3/assets/img/favicons/apple-touch-icon.png")
                        .attr("sizes", "180x180"),
                link()
                        .attr("rel", "icon")
                        .withHref("/docs/5.3/assets/img/favicons/favicon-32x32.png")
                        .attr("sizes", "32x32")
                        .withType("image/png"),
                link()
                        .attr("rel", "icon")
                        .withHref("/docs/5.3/assets/img/favicons/favicon-16x16.png")
                        .attr("sizes", "16x16")
                        .withType("image/png"),
                link()
                        .attr("rel", "manifest")
                        .withHref("/docs/5.3/assets/img/favicons/manifest.json"),
                link()
                        .attr("rel", "mask-icon")
                        .withHref("/docs/5.3/assets/img/favicons/safari-pinned-tab.svg")
                        .attr("color", "#712cf9"),
                link()
                        .attr("rel", "icon")
                        .withHref("/docs/5.3/assets/img/favicons/favicon.ico"),
                meta()
                        .withName("theme-color")
                        .attr("content", "#712cf9"),
                style("""
                                                                                   
                                                                                   .bd-placeholder-img {
                               font-size: 1.125rem;
                               text-anchor: middle;
                               -webkit-user-select: none;
                               -moz-user-select: none;
                               user-select: none;
                           }

                           @media (min-width: 768px) {
                          .bd-placeholder-img-lg {
                                   font-size: 3.5rem;
                               }
                           }

                        .b-example-divider {
                               width: 100%;
                               height: 3rem;
                               background-color: rgba(0, 0, 0,.1);
                               border: solid rgba(0, 0, 0,.15);
                               border-width: 1px 0;
                               box-shadow: inset 0.5em 1.5em rgba(0, 0, 0,.1), inset 0.125em.5em rgba(0, 0, 0,.15);
                           }

                        .b-example-vr {
                               flex-shrink: 0;
                               width: 1.5rem;
                               height: 100vh;
                           }

                        .bi {
                               vertical-align: -.125em;
                               fill: currentColor;
                           }

                        .nav-scroller {
                               position: relative;
                               z-index: 2;
                               height: 2.75rem;
                               overflow-y: hidden;
                           }

                        .nav-scroller.nav {
                               display: flex;
                               flex-wrap: nowrap;
                               padding-bottom: 1rem;
                               margin-top: -1px;
                               overflow-x: auto;
                               text-align: center;
                               white-space: nowrap;
                               -webkit-overflow-scrolling: touch;
                           }

                        .btn-bd-primary {
                               --bd-violet-bg: #712cf9;
                               --bd-violet-rgb: 112.520718, 44.062154, 249.437846;

                               --bs-btn-font-weight: 600;
                               --bs-btn-color: var(--bs-white);
                               --bs-btn-bg: var(--bd-violet-bg);
                               --bs-btn-border-color: var(--bd-violet-bg);
                               --bs-btn-hover-color: var(--bs-white);
                               --bs-btn-hover-bg: #6528e0;
                               --bs-btn-hover-border-color: #6528e0;
                               --bs-btn-focus-shadow-rgb: var(--bd-violet-rgb);
                               --bs-btn-active-color: var(--bs-btn-hover-color);
                               --bs-btn-active-bg: #5a23c8;
                               --bs-btn-active-border-color: #5a23c8;
                           }
                        .bd-mode-toggle {
                               z-index: 1500;
                           }
                           """
                ),
                link()
                        .withHref(renderModel.linkTo("/theme/cover/css/cover.css"))
                        .attr("rel", "stylesheet")

        );
    }

}
