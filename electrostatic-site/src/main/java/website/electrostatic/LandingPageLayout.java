package website.electrostatic;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static j2html.TagCreator.*;

@Slf4j
public class LandingPageLayout {

    public static Layout create() {
        return Layout.builder()
                .data(Map.of(
                        "layout", List.of("default")
                ))
                .renderFunction(LandingPageLayout::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {
        return each(
                jumboIntroduction(renderModel),
                iconGrid(renderModel),
                gettingStarted(renderModel)
        );
    }

    private static DomContent gettingStarted(RenderModel renderModel) {
        return div()
                .withClasses("container", "px-4", "py-5", "border-top")
                .with(
                        h2("Getting Started")
                                .withId("getting-started")
                                .withClasses("pb-2", "border-bottom"),
                        renderModel.getContent()
                );
    }

    private static DomContent iconGrid(RenderModel renderModel) {

        List<String> featuresList = renderModel.getPage().get("features");

        return div()
                .withClasses("container", "px-4", "py-5")
                .withId("icon-grid")
                .with(
                        h2("Features")
                                .withClasses("pb-2", "border-bottom"),
                        div()
                                .withClasses("row", "row-cols-1", "row-cols-sm-2", "row-cols-md-3", "row-cols-lg-4", "g-4", "py-5")
                                .with(
                                        each(featuresList, feature -> featureWithIcon(renderModel, feature))
                                )
                );
    }

    private static DomContent featureWithIcon(RenderModel renderModel, String feature) {

        List<String> featureParts = Stream.of(feature.split(";")).map(String::trim).toList();

        return div()
                .withClasses("col", "d-flex", "align-items-start")
                .with(
                        i().withClasses("bi", "bi-" + featureParts.get(2), "text-body-secondary", "flex-shrink-0", "me-3")
                                .attr("width", "1.75em")
                                .attr("height", "1.75em"),
                        div(
                                h3(featureParts.get(0))
                                        .withClasses("fw-bold", "mb-0", "fs-4", "text-body-emphasis"),
                                p(featureParts.get(1))
                        )
                );
    }

    private static DivTag jumboIntroduction(RenderModel renderModel) {

        List<String> navigationList = renderModel.getPage().get("navigation");

        List<String> primaryNavigation = Stream.of(navigationList.get(0).split(";")).map(String::trim).toList();
        List<String> secondaryNavigation = Stream.of(navigationList.get(1).split(";")).map(String::trim).toList();

        return div()
                .withStyle("background-image: url( " + renderModel.linkTo("/images/grid.jpg") + " ); background-repeat: no-repeat, no-repeat; background-position: right, left; background-size: cover;")
                .with(
                        div()
                                .withClasses("container", "col-xxl-8", "px-4", "py-5")
                                .with(
                                        div()
                                                .withClasses("row", "flex-lg-row-reverse", "align-items-center", "g-5", "py-5")
                                                .with(
                                                        div()
                                                                .withClasses("col-10", "col-sm-8", "col-lg-6")
                                                                .with(
                                                                        img()
                                                                                .withSrc(renderModel.linkTo("/images/grid.jpg"))
                                                                                .withClasses("d-block", "mx-lg-auto", "img-fluid")
                                                                                .attr("alt", "Bootstrap Themes")
                                                                                .attr("width", "700")
                                                                                .attr("height", "500")
                                                                                .attr("loading", "lazy")
                                                                ),
                                                        div()
                                                                .withClass("col-lg-6")
                                                                .with(
                                                                        h1(renderModel.getPage().getTitle())
                                                                                .withClasses("display-5", "fw-bold", "text-body-emphasis", "lh-1", "mb-3"),
                                                                        p(renderModel.getPage().getSubtitle())
                                                                                .withClass("lead"),
                                                                        div()
                                                                                .withClasses("d-grid", "gap-2", "d-md-flex", "justify-content-md-start")
                                                                                .with(
                                                                                        a(primaryNavigation.get(0))
                                                                                                .withHref(renderModel.linkTo(primaryNavigation.get(1)))
                                                                                                .withClasses("btn", "btn-primary", "btn-lg", "px-4", "me-md-2"),
                                                                                        a(secondaryNavigation.get(0))
                                                                                                .withHref(renderModel.linkTo(secondaryNavigation.get(1)))
                                                                                                .withClasses("btn", "btn-outline-secondary", "btn-lg", "px-4")
                                                                                )
                                                                )
                                                )
                                )
                );

    }

}
