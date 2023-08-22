package electrostatic.content.post;

import electrostatic.content.CategorisedContent;
import electrostatic.content.IndexContent;
import electrostatic.content.IndexedContent;
import electrostatic.content.TaggedContent;
import electrostatic.engine.ContentItem;
import electrostatic.engine.RenderModel;
import electrostatic.utils.IndentedCodeBlockNodeRenderer;
import electrostatic.utils.Utils;
import j2html.TagCreator;
import j2html.tags.DomContent;
import lombok.ToString;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Post implements ContentItem, TaggedContent, CategorisedContent, IndexedContent {
  private final String key;
  private final Map<String, List<String>> data;
  @ToString.Exclude
  private final Node document;
  private String url;

  public Post(String key, Map<String, List<String>> data, Node document) {
    this.key = key;
    this.data = data;
    this.document = document;
    this.url = Utils.urlFromKey(key);
  }

  public LocalDate getDate() {
    List<String> dates = data.get("date");
    if (dates == null) {
      return LocalDate.MAX;
    }
    return LocalDate.parse(dates.get(0));
  }


  public String getCategory() {
    List<String> category = data.get("category");
    if (category == null) {
      return null;
    }
    return category.get(0);
  }

  public List<String> getTags() {
    return data.getOrDefault("tags", Collections.emptyList());
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    return this.data.get("title").get(0);
  }

  public Map<String, List<String>> getData() {
    return data;
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

  public DomContent getExcerpt(RenderModel renderModel) {
    document.accept(new AbstractVisitor() {
      @Override
      public void visit(Image image) {
        image.setDestination(image.getDestination().replaceAll("\\{\\{site\\.baseurl\\}\\}", renderModel.getContext().getSite().getBaseUrl()));
        super.visit(image);
      }
    });
    HtmlRenderer renderer = HtmlRenderer.builder()
        .build();
    return TagCreator.rawHtml(
        renderer.render(document)
    );
  }

  public String getAuthor() {
    List<String> author = data.get("author");
    if (author == null) {
      return null;
    }
    return author.get(0);
  }

  public String getImage() {
    List<String> image = data.get("image");
    if (image == null) {
      return null;
    }
    return image.get(0);
  }

  @Override
  public IndexContent getIndexContent() {
    return PostIndexedContent.map(this);
  }

  public Post withDate(LocalDate date) {
    data.put("date", List.of(date.format(DateTimeFormatter.ISO_DATE)));
    return this;
  }

  public Post withKeyUrl() {
    this.url = key + ".html";
    return this;
  }

}
