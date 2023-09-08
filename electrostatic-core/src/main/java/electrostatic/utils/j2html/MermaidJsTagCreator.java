package electrostatic.utils.j2html;

import j2html.tags.DomContent;
import j2html.tags.specialized.ScriptTag;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.script;

public class MermaidJsTagCreator {

  public static ScriptTag importAndInitializeMermaidJs() {
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
