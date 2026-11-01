package io.github.teggr.electrostatic.theme.layouts;

import io.github.teggr.electrostatic.engine.Layout;
import io.github.teggr.electrostatic.engine.RenderModel;
import io.github.teggr.electrostatic.mermaid.MermaidJsTagCreator;
import io.github.teggr.electrostatic.theme.includes.Footer;
import io.github.teggr.electrostatic.theme.includes.Head;
import io.github.teggr.electrostatic.theme.includes.Header;
import io.github.teggr.electrostatic.theme.includes.PostHogTagCreator;
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
                PostHogTagCreator.create(renderModel.getContext().getSite())
            )
        );

  }

}
