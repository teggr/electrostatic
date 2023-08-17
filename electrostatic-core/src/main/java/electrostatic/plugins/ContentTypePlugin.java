package electrostatic.plugins;

import electrostatic.engine.ContentModel;

import java.nio.file.Path;

public interface ContentTypePlugin {

  void loadContent(Path sourceDirectory, ContentModel contentModel);

}
