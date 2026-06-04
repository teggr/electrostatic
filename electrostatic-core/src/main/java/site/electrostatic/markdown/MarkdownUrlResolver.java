package site.electrostatic.markdown;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.net.URI;
import java.net.URISyntaxException;

public final class MarkdownUrlResolver {

  public void resolveDocumentDestinations(Node document, String siteBaseUrl, String pagePath) {
    if (document == null) {
      return;
    }

    document.accept(new AbstractVisitor() {
      @Override
      public void visit(Image image) {
        image.setDestination(resolveDestination(image.getDestination(), siteBaseUrl, pagePath));
        super.visit(image);
      }

      @Override
      public void visit(Link link) {
        link.setDestination(resolveDestination(link.getDestination(), siteBaseUrl, pagePath));
        super.visit(link);
      }
    });
  }

  public String resolveDestination(String destination, String siteBaseUrl, String pagePath) {
    if (destination == null || destination.isBlank() || !isResolvableDestination(destination)) {
      return destination;
    }

    String absolutePath = toAbsolutePath(destination, pagePath);
    String basePath = siteBasePath(siteBaseUrl);
    if (basePath.isBlank()) {
      return absolutePath;
    }

    if (absolutePath.equals(basePath) || absolutePath.startsWith(basePath + "/")) {
      return absolutePath;
    }

    if ("/".equals(absolutePath)) {
      return basePath + "/";
    }

    return basePath + absolutePath;
  }

  public String siteBasePath(String siteBaseUrl) {
    if (siteBaseUrl == null || siteBaseUrl.isBlank()) {
      return "";
    }

    try {
      String basePath = new URI(siteBaseUrl).getPath();
      if (basePath == null || basePath.isBlank() || "/".equals(basePath)) {
        return "";
      }
      return basePath.replaceAll("/+$", "");
    } catch (URISyntaxException e) {
      return "";
    }
  }

  private String toAbsolutePath(String destination, String pagePath) {
    if (destination.startsWith("/")) {
      return destination;
    }

    String basePath = pageBasePath(pagePath);
    try {
      return URI.create(basePath).resolve(destination).getPath();
    } catch (IllegalArgumentException e) {
      return destination;
    }
  }

  private String pageBasePath(String pagePath) {
    if (pagePath == null || pagePath.isBlank()) {
      return "/";
    }

    String normalizedPagePath = pagePath.startsWith("/") ? pagePath : "/" + pagePath;
    int lastSlash = normalizedPagePath.lastIndexOf('/');
    if (lastSlash < 0) {
      return "/";
    }
    return normalizedPagePath.substring(0, lastSlash + 1);
  }

  private boolean isResolvableDestination(String destination) {
    String lowerCaseDestination = destination.toLowerCase();
    return !lowerCaseDestination.startsWith("http://")
        && !lowerCaseDestination.startsWith("https://")
        && !lowerCaseDestination.startsWith("//")
        && !lowerCaseDestination.startsWith("#")
        && !lowerCaseDestination.startsWith("mailto:")
        && !lowerCaseDestination.startsWith("tel:")
        && !lowerCaseDestination.startsWith("data:");
  }
}