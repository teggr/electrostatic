package electrostatic.theme.home;

import electrostatic.build.BuildContext;
import electrostatic.categories.CategoriesPlugin;
import electrostatic.content.book.BookPlugin;
import electrostatic.content.podcast.PodcastPlugin;
import electrostatic.content.post.DraftPostPlugin;
import electrostatic.content.post.PostPlugin;
import electrostatic.content.staticfiles.ClasspathFilesPlugin;
import electrostatic.content.staticfiles.StaticFilesPlugin;
import electrostatic.feed.FeedPlugin;
import electrostatic.index.IndexPlugin;
import electrostatic.layouts.LayoutsPlugin;
import electrostatic.pages.PagesPlugin;
import electrostatic.plugins.PluginContext;
import electrostatic.plugins.Plugins;
import electrostatic.plugins.ThemePlugin;
import electrostatic.tags.TagPlugin;
import electrostatic.theme.home.layouts.*;
import electrostatic.theme.home.pages.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class HomeThemePlugin implements ThemePlugin {

    @Override
    public void registerPlugins(PluginContext pluginContext, BuildContext buildContext) {

        PagesPlugin pagesPlugin = new PagesPlugin();
        pagesPlugin.addPage(_404Page.create());
        pagesPlugin.addPage(CategoriesPage.create());
        pagesPlugin.addPage(TagsPage.create());
        pagesPlugin.addPage(PodcastsPage.create());
        pagesPlugin.addPage(BooksPage.create());
        pagesPlugin.register();

        LayoutsPlugin layoutsPlugin = new LayoutsPlugin();
        layoutsPlugin.put("default", DefaultLayout.create());
        layoutsPlugin.put("home", HomeLayout.create());
        layoutsPlugin.put("page", PageLayout.create());
        layoutsPlugin.put("tag", TagLayout.create());
        layoutsPlugin.put("book", BookLayout.create());
        layoutsPlugin.put("podcast", PodcastLayout.create());
        layoutsPlugin.put("post", PostLayout.create());
        layoutsPlugin.register();

        BookPlugin.create().registerPlugins();
        PodcastPlugin.create().registerPlugins();
        PostPlugin.create().registerPlugins();
        StaticFilesPlugin.create("_static").registerPlugins();

        FeedPlugin.create().registerPlugins();
        DraftPostPlugin.create(buildContext.isDrafts()).registerPlugins();
        TagPlugin.create().registerPlugins();

        CategoriesPlugin.create().registerPlugins();

        IndexPlugin.create().registerPlugins();

        // TODO: jsass for sass compiling or move to ph-css or alternative
        ClasspathFilesPlugin.create(List.of(
                        "theme/default/css/main.css",
                        "theme/default/css/style.css",
                        "theme/default/images/minima-social-icons.svg"
                ))
                .registerPlugins();

    }

}
