package site.electrostatic.docs;

import java.net.URI;
import java.net.URISyntaxException;

public final class DocsMarkdownUrlResolver {

  private DocsMarkdownUrlResolver() {
  }

  public static String resolve(String destination, String docsBasePath) {
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

  public static String docsBasePath(String siteBaseUrl) {
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
}
