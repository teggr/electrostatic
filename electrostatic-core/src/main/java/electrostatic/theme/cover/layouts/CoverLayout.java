package electrostatic.theme.cover.layouts;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import electrostatic.utils.Utils;
import j2html.tags.DomContent;
import j2html.tags.specialized.BodyTag;
import j2html.tags.specialized.FooterTag;
import j2html.tags.specialized.HeaderTag;
import j2html.tags.specialized.MainTag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static electrostatic.utils.bootstrap.j2html.BootstrapTagGenerator.minBundle;
import static j2html.TagCreator.*;

@Slf4j
public class CoverLayout {

    public static Layout create() {
        return Layout.builder()
                .renderFunction(CoverLayout::render)
                .data(Map.of(
                        "layout", List.of("default")
                ))
                .build();
    }

    private static DomContent render(RenderModel renderModel) {
        log.info("rendering layout");
        return
                layoutBody(renderModel);
    }

    private static BodyTag layoutBody(RenderModel renderModel) {
        return body()
                .withClasses("d-flex", "h-100", "text-center", "text-bg-dark")
                .withStyle(String.format("""
                            background-image: url( %s );
                            background-size: cover;
                            background-position: center;
                        """, renderModel.linkTo(renderModel.getPage().getImageUrl())))
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
                        h1(renderModel.getPage().get("coverTitle").get(0))
                                .withStyle("font-family: 'Special Elite', cursive;"),
                        p(renderModel.getPage().get("coverDescription").get(0))
                                .withClass("lead"),
                        p()
                                .withClass("lead")
                                .with(
                                        a("Learn more")
                                                .withHref("#")
                                                .withClasses("btn", "btn-lg", "btn-light", "fw-bold", "border-white", "bg-white")
                                )
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
                        ),
                        p(
                                text("\nPhoto by "),
                                a("Beth Jnr")
                                        .withHref("https://unsplash.com/@bthjnr?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText"),
                                text(" on "),
                                a("Unsplash")
                                        .withHref("https://unsplash.com/photos/okUB5th8QtQ?utm_source=unsplash&utm_medium=referral&utm_content=creditCopyText")
                        )
                );
    }


}
