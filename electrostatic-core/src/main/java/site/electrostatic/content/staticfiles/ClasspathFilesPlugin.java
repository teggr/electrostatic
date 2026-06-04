package site.electrostatic.content.staticfiles;

import site.electrostatic.engine.ContentModel;
import site.electrostatic.plugins.ContentTypePlugin;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.site.Site;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
public class ClasspathFilesPlugin implements ContentTypePlugin {

  private final List<String> resourcePaths;

  public ClasspathFilesPlugin(List<String> resourcePaths) {
    this.resourcePaths = resourcePaths;
  }

  public static ClasspathFilesPlugin create(List<String> resourcePaths) {
    return new ClasspathFilesPlugin(resourcePaths);
  }

  public void registerPlugins() {
    Plugins.contentTypePlugins.add(this);
  }

  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {

    // list of classpath resources
    resourcePaths.stream()
        .peek(f -> log.info("{}", f))
        .map(ClasspathFilesPlugin::readClasspathResource)
        .forEach(contentModel::addFile);

  }

  private static StaticFile readClasspathResource(String path) {

    try {

      // Strip the theme prefix (e.g. "theme/default" or "theme/v2") to produce a web-root-relative path
      String rootPath = path.replaceAll("theme/[^/]+", "");
      InputStream resourceAsStream = ClasspathFilesPlugin.class.getClassLoader().getResourceAsStream(path);
      if (resourceAsStream == null) {
        throw new IllegalStateException("Missing classpath resource: " + path);
      }
      try (resourceAsStream) {
        return new StaticFile(
            rootPath,
            Map.of(
                "assetSourceType", List.of("classpath"),
                "assetSourcePath", List.of(path)
            ),
            resourceAsStream.readAllBytes()
        );
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}
