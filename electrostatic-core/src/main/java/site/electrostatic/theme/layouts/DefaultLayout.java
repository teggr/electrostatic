package site.electrostatic.theme.layouts;

import site.electrostatic.engine.Layout;
import site.electrostatic.engine.RenderModel;
import site.electrostatic.mermaid.MermaidJsTagCreator;
import site.electrostatic.theme.includes.Footer;
import site.electrostatic.theme.includes.Head;
import site.electrostatic.theme.includes.Header;
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
                MermaidJsTagCreator.importAndInitializeMermaidJs(),
              rawHtml("""
                  <!-- 100% privacy-first analytics -->
                  <script async src="https://scripts.simpleanalyticscdn.com/latest.js"></script>
                  """)
            )
        );

  }

}
