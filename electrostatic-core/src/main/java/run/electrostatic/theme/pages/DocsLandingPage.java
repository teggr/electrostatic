package run.electrostatic.theme.pages;

import run.electrostatic.docs.DocsSection;
import run.electrostatic.docs.DocsSectionConfig;
import run.electrostatic.engine.Page;
import run.electrostatic.engine.RenderModel;
import run.electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.p;

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
            p().withText("Browse documentation by section."),
            div()
                .withClass("docs-section-grid")
                .with(
                    each(sections, section ->
                        div()
                            .withClass("docs-section-card")
                            .with(
                                h2().withText(section.label()),
                                p().withText("View " + section.label().toLowerCase() + " documentation."),
                                a()
                                    .withHref(Utils.relativeUrl(section.indexPath()))
                                    .withText("Open")
                            )
                    )
                )
        );
  }

}
