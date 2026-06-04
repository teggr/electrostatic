package site.electrostatic.content.staticfiles;

import site.electrostatic.engine.ContentModel;
import site.electrostatic.plugins.ContentTypePlugin;
import site.electrostatic.plugins.InitializationPlugin;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.site.Site;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class StaticFilesPlugin implements ContentTypePlugin, InitializationPlugin {

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
      return new StaticFile(
          relativize.toString().replace("\\", "/"),
          Map.of(
              "assetSourceType", List.of("local"),
              "assetSourcePath", List.of(path.toAbsolutePath().normalize().toString())
          ),
          Files.readAllBytes(path)
      );

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public void registerPlugins() {
    Plugins.initializationPlugins.add(this);
    Plugins.contentTypePlugins.add(this);
  }

  @Override
  public void initialize(Path sourceDirectory) {
    try {
      Files.createDirectories(sourceDirectory.resolve(assetDirectory));
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize static directory " + assetDirectory, e);
    }
  }
}
