package site.electrostatic.docs;

import org.commonmark.Extension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.HtmlRenderer;
import site.electrostatic.engine.ContentItem;
import site.electrostatic.engine.RenderModel;
import site.electrostatic.utils.Utils;
import j2html.tags.DomContent;
import j2html.TagCreator;

import java.net.URI;
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
    String docsBasePath = docsBasePath(renderModel);
    document.accept(new AbstractVisitor() {
      @Override
      public void visit(Image image) {
        image.setDestination(resolveMarkdownDestination(image.getDestination(), docsBasePath));
        super.visit(image);
      }

      @Override
      public void visit(Link link) {
        link.setDestination(resolveMarkdownDestination(link.getDestination(), docsBasePath));
        super.visit(link);
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

  private static String resolveMarkdownDestination(String destination, String docsBasePath) {
    if (destination == null || destination.isBlank()) {
      return destination;
    }

    String resolvedDestination = destination.replace("{{site.baseurl}}", docsBasePath);
    if (!resolvedDestination.startsWith("/") || resolvedDestination.startsWith("//")) {
      return resolvedDestination;
    }

    if (docsBasePath.isBlank()) {
      return resolvedDestination;
    }

    if (resolvedDestination.equals(docsBasePath) || resolvedDestination.startsWith(docsBasePath + "/")) {
      return resolvedDestination;
    }

    if ("/".equals(resolvedDestination)) {
      return docsBasePath + "/";
    }

    return docsBasePath + resolvedDestination;
  }

  private static String docsBasePath(RenderModel renderModel) {
    try {
      String basePath = URI.create(renderModel.getContext().getSite().getBaseUrl()).getPath();
      if (basePath == null || basePath.isBlank() || "/".equals(basePath)) {
        return "";
      }
      return basePath.replaceAll("/+$", "");
    } catch (Exception ignored) {
      return "";
    }
  }

}
