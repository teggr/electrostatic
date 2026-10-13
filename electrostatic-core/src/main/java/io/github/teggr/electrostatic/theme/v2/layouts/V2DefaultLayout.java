package io.github.teggr.electrostatic.theme.v2.layouts;

import io.github.teggr.electrostatic.engine.Layout;
import io.github.teggr.electrostatic.engine.RenderModel;
import io.github.teggr.electrostatic.mermaid.MermaidJsTagCreator;
import io.github.teggr.electrostatic.theme.v2.includes.V2Footer;
import io.github.teggr.electrostatic.theme.v2.includes.V2Head;
import io.github.teggr.electrostatic.theme.v2.includes.V2Header;
import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class V2DefaultLayout {

  public static Layout create() {
    return Layout.builder()
        .renderFunction(V2DefaultLayout::render)
        .build();
  }

  public static DomContent render(RenderModel renderModel) {

    return html()
        .withLang(renderModel.getContext().getLang())
        .with(
            V2Head.create(renderModel),
            body(
                V2Header.create(renderModel),
                main()
                    .withClass("page-content")
                    .attr("aria-label", "Content")
                    .with(
                        div()
                            .withClass("wrapper")
                            .with(renderModel.getContent())
                    ),
                V2Footer.create(renderModel),
                MermaidJsTagCreator.importAndInitializeMermaidJs(),
                rawHtml("""
                    <!-- 100% privacy-first analytics -->
                    <script async src="https://scripts.simpleanalyticscdn.com/latest.js"></script>
                    """)
            )
        );

  }

}
