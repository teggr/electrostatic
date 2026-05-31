package com.robintegg.web.content.post;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.plugins.ContentTypePlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.site.Site;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class PostPlugin implements ContentTypePlugin {
  public static PostPlugin create() {
    return new PostPlugin();
  }

  @SneakyThrows
  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {

    // load posts from folder with markdown
    var postsDirectory = sourceDirectory.resolve("_posts");
    log.info("posts directory: " + postsDirectory.toAbsolutePath());

    try (Stream<Path> paths = Files.walk(postsDirectory)) {
      paths
          .filter(Files::isRegularFile)
          .peek(f -> log.info("{}", f))
          .map(PostPlugin::readPost)
          .forEach(contentModel::add);
    }
  }

  static Post readPost(Path path) {

    try {
      // Extract filename, filename without extension, and extension using Path methods
      String filename = path.getFileName().toString();
      int dotIndex = filename.lastIndexOf('.');
      String filenameWithoutExtension = (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
      String fileExtension = (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);

      if (fileExtension.equals("md")) {

        List<Extension> extensions = List.of(
            YamlFrontMatterExtension.create(),
            HeadingAnchorExtension.create()
        );
        Parser parser = Parser.builder()
            .extensions(extensions)
            .build();

        Node document = parser.parseReader(Files.newBufferedReader(path));

        YamlFrontMatterVisitor yamlFrontMatterVisitor = new YamlFrontMatterVisitor();
        document.accept(yamlFrontMatterVisitor);

        return new Post(filenameWithoutExtension, yamlFrontMatterVisitor.getData(), document);


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
