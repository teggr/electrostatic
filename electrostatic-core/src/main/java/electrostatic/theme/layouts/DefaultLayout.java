package electrostatic.theme.layouts;

import electrostatic.engine.Layout;
import electrostatic.engine.RenderModel;
import electrostatic.mermaid.MermaidJsTagCreator;
import electrostatic.theme.includes.Footer;
import electrostatic.theme.includes.Head;
import electrostatic.theme.includes.Header;
import electrostatic.theme.includes.PiwikPro;
import j2html.TagCreator;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class DefaultLayout {

  public static Layout create() {
    return Layout.builder()
        .renderFunction(DefaultLayout::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {

    return html()
        .withLang(renderModel.getContext().getLang())
        .with(
            Head.create(renderModel),
            TagCreator.body(
                TagCreator.iff(
                    "production".equals(renderModel.getContext().getEnvironment()),
                    PiwikPro.create(renderModel)
                ),
                Header.create(renderModel),
                main()
                    .withClass("page-content")
                    .attr("aria-label", "Content")
                    .with(
                        div()
                            .withClass("wrapper")
                            .with(renderModel.getContent())
                    ),
                Footer.create(renderModel),
                MermaidJsTagCreator.importAndInitializeMermaidJs()
            )
        );

  }

}
