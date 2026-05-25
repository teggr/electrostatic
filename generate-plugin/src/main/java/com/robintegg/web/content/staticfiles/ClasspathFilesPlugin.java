package com.robintegg.web.content.staticfiles;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.plugins.ContentTypePlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.site.Site;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

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
      String filename = Path.of(rootPath).getFileName().toString();
      int dotIndex = filename.lastIndexOf('.');
      String filenameWithoutExtension = (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
      String fileExtension = (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);

      InputStream resourceAsStream = ClasspathFilesPlugin.class.getClassLoader().getResourceAsStream(path);

      return new StaticFile(rootPath, Collections.emptyMap(), resourceAsStream.readAllBytes());

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}
