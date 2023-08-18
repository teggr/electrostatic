package electrostatic.theme.home;

import electrostatic.build.BuildContext;
import electrostatic.categories.CategoriesPlugin;
import electrostatic.content.book.BookPlugin;
import electrostatic.content.podcast.PodcastPlugin;
import electrostatic.content.post.DraftPostPlugin;
import electrostatic.content.post.PostPlugin;
import electrostatic.content.staticfiles.ClasspathFilesPlugin;
import electrostatic.content.staticfiles.StaticFilesPlugin;
import electrostatic.engine.ContentModel;
import electrostatic.engine.Layout;
import electrostatic.feed.FeedPlugin;
import electrostatic.index.IndexPlugin;
import electrostatic.plugins.ContentRenderPlugin;
import electrostatic.plugins.ContentTypePlugin;
import electrostatic.plugins.Plugins;
import electrostatic.plugins.ThemePlugin;
import electrostatic.tags.TagPlugin;
import electrostatic.theme.home.layouts.*;
import electrostatic.theme.home.pages.*;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class HomeThemePlugin implements ContentTypePlugin, ContentRenderPlugin, ThemePlugin {


    public static HomeThemePlugin create() {
        return new HomeThemePlugin();
    }

    @Override
    public void loadContent(Path sourceDirectory, ContentModel contentModel) {
        contentModel.addPage(_404Page.create());
        contentModel.addPage(CategoriesPage.create());
        contentModel.addPage(TagsPage.create());
        contentModel.addPage(PodcastsPage.create());
        contentModel.addPage(BooksPage.create());
    }

    @Override
    public void loadLayout(Map<String, Layout> layouts) {
        layouts.put("default", DefaultLayout.create());
        layouts.put("home", HomeLayout.create());
        layouts.put("page", PageLayout.create());
        layouts.put("tag", TagLayout.create());
        layouts.put("book", BookLayout.create());
        layouts.put("podcast", PodcastLayout.create());
        layouts.put("post", PostLayout.create());
    }

    @Override
    public void registerPlugins(BuildContext buildContext) {

        Plugins.contentTypePlugins.add(this);
        Plugins.contentRenderPlugins.add(this);

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
