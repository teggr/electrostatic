package electrostatic.website;

import electrostatic.build.BuildContext;
import electrostatic.content.docs.DocumentationPlugin;
import electrostatic.content.staticfiles.StaticFilesPlugin;
import electrostatic.layouts.LayoutsPlugin;
import electrostatic.pages.PagesPlugin;
import electrostatic.plugins.PluginContext;
import electrostatic.plugins.ThemePlugin;

public class ElectroStaticThemePlugin implements ThemePlugin {

    @Override
    public void registerPlugins(PluginContext pluginContext, BuildContext buildContext) {

        // register shared layouts
        LayoutsPlugin layoutsPlugin = new LayoutsPlugin();
        layoutsPlugin.put("default", ElectroStaticLayout.create());
        layoutsPlugin.put("landing", LandingPageLayout.create());
        layoutsPlugin.register();

        PagesPlugin.create().register();

        // register documentation plugin for _docs folder
        DocumentationPlugin.create()
                .register();

        // add static files
        StaticFilesPlugin.create("_static")
                .registerPlugins();

    }

}
