package site.electrostatic.content.podcast;

import site.electrostatic.engine.ContentModel;
import site.electrostatic.markdown.MarkdownParserFactory;
import site.electrostatic.plugins.ContentTypePlugin;
import site.electrostatic.plugins.InitializationPlugin;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.site.Site;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
public class PodcastPlugin implements ContentTypePlugin, InitializationPlugin {
  public static PodcastPlugin create() {
    return new PodcastPlugin();
  }

  @SneakyThrows
  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {
    // load podcasts from folder with markdown
    var podcastDirectory = sourceDirectory.resolve("_podcasts");
    log.info("podcasts directory: " + podcastDirectory.toAbsolutePath());

    try (Stream<Path> paths = Files.walk(podcastDirectory)) {
      paths
          .filter(Files::isRegularFile)
          .peek(f -> log.info("{}", f))
          .map(PodcastPlugin::readPodcast)
          .forEach(contentModel::add);
    }


  }

  private static Podcast readPodcast(Path path) {

    try {
      // Extract filename, filename without extension, and extension using Path methods
      String filename = path.getFileName().toString();
      int dotIndex = filename.lastIndexOf('.');
      String filenameWithoutExtension = (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
      String fileExtension = (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);

      if (fileExtension.equals("md")) {
        Parser parser = MarkdownParserFactory.create();

        Node document = parser.parseReader(Files.newBufferedReader(path));

        YamlFrontMatterVisitor yamlFrontMatterVisitor = new YamlFrontMatterVisitor();
        document.accept(yamlFrontMatterVisitor);

        return new Podcast(filenameWithoutExtension, yamlFrontMatterVisitor.getData(), document);

      }

      return null;

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
      Files.createDirectories(sourceDirectory.resolve("_podcasts"));
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize _podcasts directory", e);
    }
  }

}
