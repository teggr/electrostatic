package site.electrostatic.theme.pages;

import site.electrostatic.docs.DocsSection;
import site.electrostatic.docs.DocsSectionConfig;
import site.electrostatic.engine.Page;
import site.electrostatic.engine.RenderModel;
import site.electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.ol;
import static j2html.TagCreator.p;
import static j2html.TagCreator.li;

public class DocsLandingPage {

  public static Page create() {
    return Page.builder()
        .path("/index.html")
        .data(Map.of(
            "layout", List.of("default"),
            "title", List.of("Documentation")
        ))
        .renderFunction(DocsLandingPage::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {
    List<DocsSection> sections = DocsSectionConfig.fromSite(renderModel.getContext().getSite());

    return div()
        .withClass("docs-landing")
        .with(
            h1().withText(renderModel.getContext().getSite().getTitle()),
                        p().withText("Build and publish documentation sites with Electrostatic."),
                        p().withText("Use this reference to install the toolchain, choose a theme, and configure plugins."),
                        h2().withText("Quick Start"),
                        ol().with(
                                li().withText("Install via JBang or Maven plugin."),
                                li().withText("Initialize a site with your preferred theme."),
                                li().withText("Write content, then run generate or serve.")
                        ),
            div()
                .withClass("docs-section-grid")
                .with(
                    each(sections, section ->
                        div()
                            .withClass("docs-section-card")
                            .with(
                                h2().withText(section.label()),
                                                                p().withText(describeSection(section.label())),
                                a()
                                    .withHref(Utils.relativeUrl(section.indexPath()))
                                                                        .withText("Browse")
                            )
                    )
                )
        );
  }

    private static String describeSection(String label) {
        return switch (label.toLowerCase()) {
            case "installation" -> "Set up Electrostatic for local development and CI builds.";
            case "guides" -> "Follow practical workflows for themes and day-to-day authoring.";
            case "plugins" -> "Understand built-in plugins and when to use each one.";
            default -> "Explore this section for " + label.toLowerCase() + " docs.";
        };
    }

}
