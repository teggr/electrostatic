package electrostatic.utils;

import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IndentedCodeBlockNodeRenderer implements NodeRenderer {

    private final HtmlWriter html;
    private final HtmlNodeRendererContext context;

    public IndentedCodeBlockNodeRenderer(HtmlNodeRendererContext context) {
        this.html = context.getWriter();
        this.context = context;
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        // Return the node types we want to use this renderer for.
        return Set.of(FencedCodeBlock.class);
    }

    @Override
    public void render(Node node) {
        FencedCodeBlock fencedCodeBlock = (FencedCodeBlock) node;
        String literal = fencedCodeBlock.getLiteral();
        Map<String, String> attributes = new LinkedHashMap<>();
        String info = fencedCodeBlock.getInfo();
        boolean mermaid = false;
        if (info != null && !info.isEmpty()) {
            int space = info.indexOf(" ");
            String language;
            if (space == -1) {
                language = info;
            } else {
                language = info.substring(0, space);
            }
            mermaid = "mermaid".equals(language);
            if (mermaid) {
                attributes.put("class", language);
            } else {
                attributes.put("class", "language-" + language);
            }
        }
        renderCodeBlock(literal, fencedCodeBlock, attributes, mermaid);
    }

    private void renderCodeBlock(String literal, Node node, Map<String, String> attributes, boolean mermaid) {
        html.line();
        if (mermaid) {
            html.tag("pre", getAttrs(node, "pre", attributes));
            html.text(literal);
            html.tag("/pre");
        } else {
            html.tag("pre", getAttrs(node, "pre"));
            html.tag("code", getAttrs(node, "code", attributes));
            html.text(literal);
            html.tag("/code");
            html.tag("/pre");
        }
        html.line();
    }

    private Map<String, String> getAttrs(Node node, String tagName) {
        return getAttrs(node, tagName, Collections.<String, String>emptyMap());
    }

    private Map<String, String> getAttrs(Node node, String tagName, Map<String, String> defaultAttributes) {
        return context.extendAttributes(node, tagName, defaultAttributes);
    }

}
