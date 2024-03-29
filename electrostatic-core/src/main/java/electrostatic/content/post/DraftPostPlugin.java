package electrostatic.content.post;

import electrostatic.engine.ContentModel;
import electrostatic.plugins.ContentTypePlugin;
import electrostatic.plugins.Plugins;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
public class DraftPostPlugin implements ContentTypePlugin {

  public static DraftPostPlugin create(boolean drafts) {
    log.info("includeDrafts={}", drafts);
    return new DraftPostPlugin(drafts);
  }

  private final boolean includeDrafts;

  public DraftPostPlugin(boolean includeDrafts) {
    this.includeDrafts = includeDrafts;
  }

  @SneakyThrows
  @Override
  public void loadContent(Path sourceDirectory, ContentModel contentModel) {

    if (!includeDrafts) {
      log.info("not including drafts");
      return;
    }

    // load posts from folder with markdown
    var postsDirectory = sourceDirectory.resolve("_drafts");
    log.info("drafts directory: " + postsDirectory.toAbsolutePath());

    if(Files.exists(postsDirectory)) {
      try (Stream<Path> paths = Files.walk(postsDirectory)) {
        paths
                .filter(Files::isRegularFile)
                .peek(f -> log.info("{}", f))
                .map(DraftPostPlugin::readPost)
                .forEach(contentModel::add);
      }
    }
  }

  static Post readPost(Path path) {

    Post post = PostPlugin.readPost(path);
    return post.withDate(LocalDate.now()).withKeyUrl();

  }

  public void registerPlugins() {
    Plugins.contentTypePlugins.add(this);
  }
}
