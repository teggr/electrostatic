package electrostatic.theme.cover;

import electrostatic.build.BuildContext;
import electrostatic.content.staticfiles.ClasspathFilesPlugin;
import electrostatic.content.staticfiles.StaticFilesPlugin;
import electrostatic.layouts.LayoutsPlugin;
import electrostatic.pages.PagesPlugin;
import electrostatic.plugins.PluginContext;
import electrostatic.plugins.ThemePlugin;
import electrostatic.theme.cover.layouts.ContactLayout;
import electrostatic.theme.cover.layouts.CoverLayout;
import electrostatic.theme.cover.layouts.CoverThemeDefaultLayout;

import java.util.List;

public class CoverThemePlugin implements ThemePlugin {

    @Override
    public void registerPlugins(PluginContext pluginContext, BuildContext buildContext) {

        PagesPlugin pagesPlugin = new PagesPlugin();
       // pagesPlugin.addPage(_404Page.create());
        pagesPlugin.register();

        LayoutsPlugin layoutsPlugin = new LayoutsPlugin();
        layoutsPlugin.put("default", CoverThemeDefaultLayout.create());
        layoutsPlugin.put("landing", CoverLayout.create());
        layoutsPlugin.put("contact", ContactLayout.create());
        layoutsPlugin.register();

        StaticFilesPlugin.create("_static")
                .registerPlugins();

        // TODO: jsass for sass compiling or move to ph-css or alternative
        ClasspathFilesPlugin.create(List.of(
                        "theme/cover/css/cover.css"
                ))
                .registerPlugins();

    }

}
