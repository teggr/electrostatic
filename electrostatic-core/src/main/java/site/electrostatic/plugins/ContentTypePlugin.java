package site.electrostatic.plugins;

import site.electrostatic.engine.ContentModel;
import site.electrostatic.site.Site;

import java.nio.file.Path;

public interface ContentTypePlugin {

  void loadContent(Path sourceDirectory, Site site, ContentModel contentModel);

}
