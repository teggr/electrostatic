package com.robintegg.web.content.staticfiles;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.plugins.ContentTypePlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.site.Site;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

@Slf4j
public class StaticFilesPlugin implements ContentTypePlugin {

  private final String assetDirectory;

  public StaticFilesPlugin(String assetDirectory) {
    this.assetDirectory = assetDirectory;
  }

  public static StaticFilesPlugin create(String assetDirectory) {
    return new StaticFilesPlugin(assetDirectory);
  }

  @SneakyThrows
  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {
// load filers from all none special folders
    var staticDirectory = sourceDirectory.resolve(assetDirectory);
    log.info("static directory: " + staticDirectory.toAbsolutePath());

    try (Stream<Path> paths = Files.walk(staticDirectory)) {
      paths
          .filter(Files::isRegularFile)
          .peek(f -> log.info("{}", f))
          .map( p -> this.readFile(p,staticDirectory))
          .forEach(contentModel::addFile);
    }

  }

  private StaticFile readFile(Path path, Path staticDirectory) {

    try {

      Path relativize = staticDirectory.relativize(path);

      // Extract filename, filename without extension, and extension using Path methods
      String filename = path.getFileName().toString();
      int dotIndex = filename.lastIndexOf('.');
      String filenameWithoutExtension = (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
      String fileExtension = (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);

      return new StaticFile(relativize.toString(), Collections.emptyMap(), Files.readAllBytes(path));

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public void registerPlugins() {
    Plugins.contentTypePlugins.add(this);
  }
}
