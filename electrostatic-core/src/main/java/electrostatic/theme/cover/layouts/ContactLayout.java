package electrostatic.theme.cover.layouts;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.tags.DomContent;
import j2html.tags.specialized.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static electrostatic.utils.bootstrap.j2html.BootstrapTagGenerator.*;
import static electrostatic.utils.googlefonts.j2html.GoogleFontsTagGenerator.*;
import static j2html.TagCreator.*;

@Slf4j
public class ContactLayout {

    public static Layout create() {
        return Layout.builder()
                .renderFunction(ContactLayout::render)
                .data(Map.of(
                        "layout", List.of("default")
                ))
                .build();
    }

    private static DomContent render(RenderModel renderModel) {
        log.info("rendering contact layout");
        return
                layoutBody(renderModel);
    }

    private static BodyTag layoutBody(RenderModel renderModel) {
        return body()
                .withClasses("d-flex", "h-100", "text-center", "text-bg-dark")
                .with(
                        div()
                                .withClasses("cover-container", "d-flex", "w-100", "h-100", "p-3", "mx-auto", "flex-column")
                                .with(
                                        layoutHeader(renderModel),
                                        layoutContent(renderModel),
                                        layoutFooter(renderModel)
                                ),
                        minBundle()
                );
    }

    private static MainTag layoutContent(RenderModel renderModel) {
        return main()
                .withClass("px-3")
                .with(
                        h1("Contact Details"),
                        a(renderModel.getPage().get("email").get(0))
                                .withHref("emailto:" + renderModel.getPage().get("email"))
                                .withClass("lead")
                );
    }

    private static HeaderTag layoutHeader(RenderModel renderModel) {
        return header()
                .withClass("mb-auto")
                .with(
                        div(
                                a()
                                        .withHref(renderModel.linkTo("/"))
                                        .with(
                                                h3(renderModel.getPage().get("brandName").isEmpty() ? "" : renderModel.getPage().get("brandName").get(0))
                                                        .withClasses("float-md-start", "mb-0")
                                                        .withStyle("font-family: 'Special Elite', cursive;")
                                        ),
                                nav()
                                        .withClasses("nav", "nav-masthead", "justify-content-center", "float-md-end")
                                        .with(
                                                each(renderModel.getContentModel().getPages(), myPage -> {
                                                    return iff(
                                                            myPage.isIncludeMenu(),
                                                            a()
                                                                    .withClasses("nav-link", "fw-bold", "py-1", "px-0", "active")
                                                                    .withHref(Utils.relativeUrl(myPage.getUrl()))
                                                                    .withText(Utils.escape(myPage.getTitle()))
                                                    );
                                                })
                                        )
                        )
                );
    }

    private static FooterTag layoutFooter(RenderModel renderModel) {
        return footer()
                .withClasses("mt-auto", "text-white-50")
                .with(
                        p(
                                text("Cover theme for "),
                                a("electrostatic")
                                        .withHref("https://electrostatic.website")
                                        .withClass("text-white"),
                                text(", by "),
                                a("r3l software")
                                        .withHref("https://r3l.software")
                                        .withClass("text-white"),
                                text(".")
                        )
                );
    }


}
