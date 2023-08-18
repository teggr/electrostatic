package electrostatic.theme.product;

import electrostatic.build.BuildContext;
import electrostatic.content.staticfiles.ClasspathFilesPlugin;
import electrostatic.content.staticfiles.StaticFilesPlugin;
import electrostatic.engine.ContentModel;
import electrostatic.engine.Layout;
import electrostatic.engine.Page;
import electrostatic.plugins.ContentRenderPlugin;
import electrostatic.plugins.ContentTypePlugin;
import electrostatic.plugins.Plugins;
import electrostatic.plugins.ThemePlugin;
import electrostatic.theme.home.pages._404Page;
import electrostatic.theme.product.layouts.ProductDefaultLayout;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ProductThemePlugin
        implements ContentTypePlugin, ContentRenderPlugin, ThemePlugin {

    public static ProductThemePlugin create() {
        return new ProductThemePlugin();
    }

    private Page landingPage;

    public ProductThemePlugin landingPage(Page landingPage) {
        this.landingPage = landingPage;
        return this;
    }

    @Override
    public void loadContent(Path sourceDirectory, ContentModel contentModel) {
        contentModel.addPage(_404Page.create());
        contentModel.addPage(landingPage);
    }

    @Override
    public void loadLayout(Map<String, Layout> layouts) {
        layouts.put("default", ProductDefaultLayout.create());
    }

    @Override
    public void registerPlugins(BuildContext buildContext) {

        Plugins.contentTypePlugins.add(this);
        Plugins.contentRenderPlugins.add(this);

    }

}
