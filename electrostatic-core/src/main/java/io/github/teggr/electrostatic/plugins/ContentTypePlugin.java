package io.github.teggr.electrostatic.plugins;

import io.github.teggr.electrostatic.engine.ContentModel;
import io.github.teggr.electrostatic.site.Site;

import java.nio.file.Path;

public interface ContentTypePlugin {

  void loadContent(Path sourceDirectory, Site site, ContentModel contentModel);

}
