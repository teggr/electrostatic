package com.robintegg.web.mermaid;

import j2html.tags.DomContent;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.script;

public class MermaidJsTagCreator {

  public static DomContent importAndInitializeMermaidJs() {
    return
        script()
            .withType("module")
            .with(
                rawHtml(
                    """
                        import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
                        mermaid.initialize({ startOnLoad: true });
                        """)
            );
  }

}
