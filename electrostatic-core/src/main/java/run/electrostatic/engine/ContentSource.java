package run.electrostatic.engine;

import run.electrostatic.plugins.Plugins;
import run.electrostatic.site.Site;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class ContentSource {

  private final Site site;
  private final Path sourceDirectory;

  public ContentSource(Site site, Path sourceDirectory) {
    this.site = site;
    this.sourceDirectory = sourceDirectory;
  }

  public void loadContent(ContentModel contentModel) {

    log.info("source directory: {}", sourceDirectory.toAbsolutePath());

    Plugins.contentTypePlugins.stream()
        .forEach(contentTypePlugin -> {
          contentTypePlugin.loadContent(sourceDirectory, site, contentModel);
        });

  }

}
