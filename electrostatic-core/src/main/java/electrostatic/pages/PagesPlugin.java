package electrostatic.pages;

import electrostatic.engine.ContentModel;
import electrostatic.engine.Page;
import electrostatic.plugins.ContentTypePlugin;
import electrostatic.plugins.Plugins;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PagesPlugin implements ContentTypePlugin {

    public final List<Page> pages = new ArrayList<>();

    public void addPage(Page page) {
        pages.add(page);
    }

    @Override
    public void loadContent(Path sourceDirectory, ContentModel contentModel) {
        pages.forEach(contentModel::addPage);
    }

    public void register() {
        Plugins.contentTypePlugins.add(this);
    }

}
