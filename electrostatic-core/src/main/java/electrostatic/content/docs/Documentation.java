package electrostatic.content.docs;

import electrostatic.content.TaggedContent;
import electrostatic.engine.ContentItem;
import electrostatic.engine.RenderModel;
import electrostatic.utils.IndentedCodeBlockNodeRenderer;
import j2html.TagCreator;
import j2html.tags.DomContent;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.each;

@RequiredArgsConstructor
public class Documentation implements ContentItem, TaggedContent {

    private final String key;
    private final Map<String, List<String>> data;
    @ToString.Exclude
    private final Node document;
    private final String url;

    public Documentation(String key, Map<String, List<String>> data, Node document) {
        this.key = key;
        this.data = data;
        this.document = document;
        this.url = "/docs/" + key + ".html";
    }
    public List<String> getTags() {
        return data.getOrDefault("tags", Collections.emptyList());
    }

    public Map<String, List<String>> getData() {
        return data;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return data.get("title").get(0);
    }

    public String getSubtitle() {
        List<String> stringList = data.get("subtitle");
        if(stringList == null) {
            return "";
        }
        return stringList.get(0);
    }

    public LocalDate getDate() {
        List<String> dates = data.get("date");
        if (dates == null) {
            return LocalDate.MAX;
        }
        return LocalDate.parse(dates.get(0));
    }

    public DomContent getExcerpt(RenderModel renderModel) {
        return each();
    }

    public DomContent getContent(RenderModel renderModel) {
        document.accept(new AbstractVisitor() {
            @Override
            public void visit(Image image) {
                image.setDestination(image.getDestination().replaceAll("\\{\\{site\\.baseurl\\}\\}", renderModel.getContext().getSite().getBaseUrl()));
                super.visit(image);
            }
        });
        HtmlRenderer renderer = HtmlRenderer.builder()
                .nodeRendererFactory(new HtmlNodeRendererFactory() {
                    public NodeRenderer create(HtmlNodeRendererContext context) {
                        return new IndentedCodeBlockNodeRenderer(context);
                    }
                })
                .build();
        return TagCreator.rawHtml(
                renderer.render(document)
        );

    }

}

