package site.electrostatic.docs;

import org.commonmark.Extension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.HtmlRenderer;
import site.electrostatic.engine.ContentItem;
import site.electrostatic.engine.RenderModel;
import site.electrostatic.utils.Utils;
import j2html.tags.DomContent;
import j2html.TagCreator;

import java.util.List;
import java.util.Map;

public class DocsEntry implements ContentItem {

  private final DocsSection section;
  private final String slug;
  private final String title;
  private final String description;
  private final int order;
  private final String url;
  private final Node document;
  private final Map<String, List<String>> data;

  public DocsEntry(
      DocsSection section,
      String slug,
      String title,
      String description,
      int order,
      String url,
      Node document,
      Map<String, List<String>> data
  ) {
    this.section = section;
    this.slug = slug;
    this.title = title;
    this.description = description;
    this.order = order;
    this.url = url;
    this.document = document;
    this.data = data;
  }

  public DocsSection getSection() {
    return section;
  }

  public String getSlug() {
    return slug;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public int getOrder() {
    return order;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public Map<String, List<String>> getData() {
    return data;
  }

  @Override
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
        .build();

    return TagCreator.rawHtml(renderer.render(document));
  }

  public DomContent getExcerpt(RenderModel renderModel) {
    if (description == null || description.isBlank()) {
      return TagCreator.each();
    }
    return TagCreator.p(Utils.escape(description));
  }

}
