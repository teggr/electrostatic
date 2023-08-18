package electrostatic.content.podcast;

import electrostatic.engine.ContentModel;
import electrostatic.plugins.ContentTypePlugin;
import electrostatic.plugins.Plugins;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class PodcastPlugin implements ContentTypePlugin {
  public static PodcastPlugin create() {
    return new PodcastPlugin();
  }

  @SneakyThrows
  @Override
  public void loadContent(Path sourceDirectory, ContentModel contentModel) {
    // load podcasts from folder with markdown
    var podcastDirectory = sourceDirectory.resolve("_podcasts");
    log.info("podcasts directory: " + podcastDirectory.toAbsolutePath());

    if(Files.exists(podcastDirectory)) {
      try (Stream<Path> paths = Files.walk(podcastDirectory)) {
        paths
                .filter(Files::isRegularFile)
                .peek(f -> log.info("{}", f))
                .map(PodcastPlugin::readPodcast)
                .forEach(contentModel::add);
      }
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

        Extension extension = YamlFrontMatterExtension.create();
        Parser parser = Parser.builder()
            .extensions(List.of(extension))
            .build();

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
    Plugins.contentTypePlugins.add(this);
  }

}
