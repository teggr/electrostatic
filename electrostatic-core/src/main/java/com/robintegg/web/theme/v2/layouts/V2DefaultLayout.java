package com.robintegg.web.theme.v2.layouts;

import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.mermaid.MermaidJsTagCreator;
import com.robintegg.web.theme.v2.includes.V2Footer;
import com.robintegg.web.theme.v2.includes.V2Head;
import com.robintegg.web.theme.v2.includes.V2Header;
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
