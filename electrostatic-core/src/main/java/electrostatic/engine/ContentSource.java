package electrostatic.engine;

import electrostatic.plugins.Plugins;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class ContentSource {

  private final Path sourceDirectory;

  public ContentSource(Path sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
  }

  public void loadContent(ContentModel contentModel) {

    log.info("source directory: {}", sourceDirectory.toAbsolutePath());

    Plugins.contentTypePlugins.stream()
        .forEach(contentTypePlugin -> {
          contentTypePlugin.loadContent(sourceDirectory, contentModel);
        });

  }

}
