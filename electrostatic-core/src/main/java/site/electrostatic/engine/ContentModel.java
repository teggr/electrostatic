package site.electrostatic.engine;

import site.electrostatic.content.staticfiles.StaticFile;
import site.electrostatic.plugins.Plugins;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@ToString
public class ContentModel {
  private static final String ASSET_SOURCE_TYPE = "assetSourceType";
  private static final String ASSET_SOURCE_PATH = "assetSourcePath";

  // TODO: these should be streamed to the rendering engine
  private final List<Page> pages = new ArrayList<>();
  private final List<StaticFile> files = new ArrayList<>();
  private final List<ContentItem> items = new ArrayList<>();

  public void add(ContentItem contentItem) {
    // TODO: this would be where the item would be passed
    // onto the next stage rather than stored. only the aggregator
    // plugins would be interested in the stored content
    this.items.add(contentItem);
    Plugins.aggregatorPlugins
        .forEach(aggregatorPlugin -> aggregatorPlugin.add(contentItem));
  }

  public void addFile(StaticFile staticFile) {
    String incomingPath = normalizePath(staticFile.getPath());
    for (int index = 0; index < files.size(); index++) {
      StaticFile existingFile = files.get(index);
      if (!normalizePath(existingFile.getPath()).equals(incomingPath)) {
        continue;
      }

      boolean existingLocal = isLocalAsset(existingFile);
      boolean incomingLocal = isLocalAsset(staticFile);
      if (existingLocal && !incomingLocal) {
        log.warn("Static asset conflict at {}: {} overrides {}", incomingPath, source(existingFile), source(staticFile));
        return;
      }

      log.warn("Static asset conflict at {}: {} overrides {}", incomingPath, source(staticFile), source(existingFile));
      files.set(index, staticFile);
      return;
    }

    this.files.add(staticFile);
  }

  public List<String> getLocalCssPaths() {
    return files.stream()
        .filter(ContentModel::isLocalAsset)
        .map(StaticFile::getPath)
        .map(ContentModel::normalizePath)
        .filter(path -> path.toLowerCase(Locale.ROOT).endsWith(".css"))
        .distinct()
        .sorted()
        .toList();
  }

  public void addPage(Page page) {
    this.pages.add(page);
  }

  // TODO: is this entry point or the plugins? plugins would need to
  // have all the information before creating any container pages
  // currently we wait until the processing occurs
  public <T> List<T> getContentOfType(Class<T> clazz) {
    return this.items.stream()
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .toList();
  }

  public void visit(ContentModelVisitor visitor) {

    // pages
    pages
        .forEach(visitor::page);

    // content model
    items.stream()
        .map(item ->
        {
          log.info("item={}", item);

          return Page.builder()
              .data(item.getData())
              .path(item.getUrl())
              .renderFunction(item::getContent)
              .build();
        })
        .forEach(visitor::page);

    // raw contents
    files
        .forEach(visitor::file);

    Plugins.aggregatorPlugins
        .forEach(aggregatorPlugin -> aggregatorPlugin.visit(visitor));

  }

  // TODO: replace with menu plugin?
  public List<Page> getPages() {
    return pages;
  }

  private static String normalizePath(String path) {
    String normalized = path.replace("\\", "/");
    return normalized.startsWith("/") ? normalized : "/" + normalized;
  }

  private static boolean isLocalAsset(StaticFile staticFile) {
    return "local".equals(firstValue(staticFile, ASSET_SOURCE_TYPE));
  }

  private static String source(StaticFile staticFile) {
    String sourceType = firstValue(staticFile, ASSET_SOURCE_TYPE);
    if (sourceType == null) {
      sourceType = "unknown";
    }

    String sourcePath = firstValue(staticFile, ASSET_SOURCE_PATH);
    if (sourcePath == null) {
      sourcePath = staticFile.getPath();
    }
    return sourceType + ":" + sourcePath;
  }

  private static String firstValue(StaticFile staticFile, String key) {
    List<String> values = staticFile.getData().get(key);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

}
