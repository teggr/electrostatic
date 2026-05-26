package com.robintegg.web.plugins;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.site.Site;

import java.nio.file.Path;

public interface ContentTypePlugin {

  void loadContent(Path sourceDirectory, Site site, ContentModel contentModel);

}
