package run.electrostatic.content.post;

import run.electrostatic.engine.ContentModel;
import run.electrostatic.plugins.ContentTypePlugin;
import run.electrostatic.plugins.InitializationPlugin;
import run.electrostatic.plugins.Plugins;
import run.electrostatic.site.Site;
import run.electrostatic.core.GenerationOptions;
import run.electrostatic.core.GenerationOptionsContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
public class DraftPostPlugin implements ContentTypePlugin, InitializationPlugin {

  public static DraftPostPlugin create() {
    GenerationOptions options = GenerationOptionsContext.current();
    log.info("includeDrafts={}", options.includeDrafts());
    return new DraftPostPlugin(options.includeDrafts());
  }

  private final boolean includeDrafts;

  public DraftPostPlugin(boolean includeDrafts) {
    this.includeDrafts = includeDrafts;
  }

  @SneakyThrows
  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {

    if (!includeDrafts) {
      log.info("not including drafts");
      return;
    }

    // load posts from folder with markdown
    var postsDirectory = sourceDirectory.resolve("_drafts");
    log.info("drafts directory: " + postsDirectory.toAbsolutePath());

    try (Stream<Path> paths = Files.walk(postsDirectory)) {
      paths
          .filter(Files::isRegularFile)
          .peek(f -> log.info("{}", f))
          .map(DraftPostPlugin::readPost)
          .forEach(contentModel::add);
    }
  }

  static Post readPost(Path path) {

    Post post = PostPlugin.readPost(path);
    return post.withDate(LocalDate.now()).withKeyUrl();

  }

  public void registerPlugins() {
    Plugins.initializationPlugins.add(this);
    Plugins.contentTypePlugins.add(this);
  }

  @Override
  public void initialize(Path sourceDirectory) {
    try {
      Files.createDirectories(sourceDirectory.resolve("_drafts"));
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize _drafts directory", e);
    }
  }
}
