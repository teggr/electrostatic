package run.electrostatic.plugins;

import run.electrostatic.engine.ContentModel;
import run.electrostatic.site.Site;

import java.nio.file.Path;

public interface ContentTypePlugin {

  void loadContent(Path sourceDirectory, Site site, ContentModel contentModel);

}
