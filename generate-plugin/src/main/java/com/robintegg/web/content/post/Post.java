package com.robintegg.web.content.post;

import com.robintegg.web.content.CategorisedContent;
import com.robintegg.web.content.IndexContent;
import com.robintegg.web.content.IndexedContent;
import com.robintegg.web.content.TaggedContent;
import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.utils.Utils;
import j2html.TagCreator;
import j2html.tags.DomContent;
import lombok.ToString;
import org.commonmark.Extension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

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
    List<Extension> extensions = List.of(HeadingAnchorExtension.create());
    HtmlRenderer renderer = HtmlRenderer.builder()
        .extensions(extensions)
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
    // Check if description field exists in front matter
    List<String> description = data.get("description");
    if (description != null && !description.isEmpty()) {
      return TagCreator.rawHtml(description.get(0));
    }
    
    // Otherwise, extract and render the first paragraph
    FirstParagraphExtractor extractor = new FirstParagraphExtractor();
    document.accept(extractor);
    Node firstParagraph = extractor.getFirstParagraph();
    
    if (firstParagraph != null) {
      firstParagraph.accept(new AbstractVisitor() {
        @Override
        public void visit(Image image) {
          image.setDestination(image.getDestination().replaceAll("\\{\\{site\\.baseurl\\}\\}", renderModel.getContext().getSite().getBaseUrl()));
          super.visit(image);
        }
      });
      HtmlRenderer renderer = HtmlRenderer.builder()
          .build();
      return TagCreator.rawHtml(
          renderer.render(firstParagraph)
      );
    }
    
    // Fallback to empty if no paragraph found
    return TagCreator.each();
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

  static class FirstParagraphExtractor extends AbstractVisitor {
    private Node firstParagraph = null;

    @Override
    public void visit(Paragraph paragraph) {
      if (firstParagraph == null) {
        firstParagraph = paragraph;
      }
      // Don't call super.visit to avoid traversing further
    }

    public Node getFirstParagraph() {
      return firstParagraph;
    }
  }

  class IndentedCodeBlockNodeRenderer implements NodeRenderer {

    private final HtmlWriter html;
    private final HtmlNodeRendererContext context;

    IndentedCodeBlockNodeRenderer(HtmlNodeRendererContext context) {
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

}
