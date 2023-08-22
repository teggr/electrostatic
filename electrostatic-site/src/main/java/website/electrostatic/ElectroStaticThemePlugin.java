package website.electrostatic;

import electrostatic.build.BuildContext;
import electrostatic.content.docs.DocumentationPlugin;
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
        layoutsPlugin.register();

        // register a landing page
        PagesPlugin pagesPlugin = new PagesPlugin();
        pagesPlugin.addPage(LandingPage.create());
        pagesPlugin.register();

        // static resources

        // register documentation plugin for _docs folder
        DocumentationPlugin.create().register();

    }

}
