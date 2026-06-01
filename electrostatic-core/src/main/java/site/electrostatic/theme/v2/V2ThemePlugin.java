package site.electrostatic.theme.v2;

import site.electrostatic.content.book.BookPlugin;
import site.electrostatic.content.feed.FeedSubscriptionPlugin;
import site.electrostatic.content.podcast.PodcastPlugin;
import site.electrostatic.content.post.DraftPostPlugin;
import site.electrostatic.content.post.PostPlugin;
import site.electrostatic.content.staticfiles.ClasspathFilesPlugin;
import site.electrostatic.content.staticfiles.StaticFilesPlugin;
import site.electrostatic.engine.ContentModel;
import site.electrostatic.engine.Layout;
import site.electrostatic.engine.Pageable;
import site.electrostatic.feed.FeedPlugin;
import site.electrostatic.github.GithubActivityPlugin;
import site.electrostatic.index.IndexPlugin;
import site.electrostatic.plugins.ContentRenderPlugin;
import site.electrostatic.plugins.ContentTypePlugin;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.plugins.ThemePlugin;
import site.electrostatic.posts.PostsPlugin;
import site.electrostatic.site.Site;
import site.electrostatic.tags.TagPlugin;
import site.electrostatic.theme.layouts.BookLayout;
import site.electrostatic.theme.layouts.CategoryLayout;
import site.electrostatic.theme.layouts.PageLayout;
import site.electrostatic.theme.layouts.PodcastLayout;
import site.electrostatic.theme.layouts.PostsLayout;
import site.electrostatic.theme.layouts.TagLayout;
import site.electrostatic.theme.pages.BooksPage;
import site.electrostatic.theme.pages.FeedsPage;
import site.electrostatic.theme.pages.PodcastsPage;
import site.electrostatic.theme.pages.PostsPage;
import site.electrostatic.theme.pages.TagsPage;
import site.electrostatic.theme.pages._404Page;
import site.electrostatic.theme.v2.layouts.V2DefaultLayout;
import site.electrostatic.theme.v2.layouts.V2HomeLayout;
import site.electrostatic.theme.v2.layouts.V2PostLayout;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class V2ThemePlugin implements ContentTypePlugin, ContentRenderPlugin, ThemePlugin {

  public static V2ThemePlugin create() {
    return new V2ThemePlugin();
  }

  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {
    contentModel.addPage(_404Page.create());
    contentModel.addPage(TagsPage.create());
    contentModel.addPage(PodcastsPage.create());
    contentModel.addPage(BooksPage.create());
    contentModel.addPage(FeedsPage.create());
    contentModel.addPage(PostsPage.create("/posts/index.html", new Pageable(1, 10)));
  }

  @Override
  public void loadLayout(Map<String, Layout> layouts) {
    layouts.put("default", V2DefaultLayout.create());
    layouts.put("home", V2HomeLayout.create());
    layouts.put("page", PageLayout.create());
    layouts.put("tag", TagLayout.create());
    layouts.put("book", BookLayout.create());
    layouts.put("podcast", PodcastLayout.create());
    layouts.put("post", V2PostLayout.create());
    layouts.put("posts", PostsLayout.create());
    layouts.put("category", CategoryLayout.create());
  }

  @Override
  public void registerPlugins() {

    Plugins.contentTypePlugins.add(this);
    Plugins.contentRenderPlugins.add(this);

    BookPlugin.create().registerPlugins();
    PodcastPlugin.create().registerPlugins();
    PostPlugin.create().registerPlugins();
    FeedSubscriptionPlugin.create().registerPlugins();
    StaticFilesPlugin.create("_static").registerPlugins();
    GithubActivityPlugin.create().registerPlugins();

    FeedPlugin.create().registerPlugins();
    DraftPostPlugin.create().registerPlugins();
    TagPlugin.create().registerPlugins();
    IndexPlugin.create().registerPlugins();
    PostsPlugin.create().registerPlugins();

    ClasspathFilesPlugin.create(List.of(
            "theme/v2/css/theme.css",
            "theme/default/images/minima-social-icons.svg"
        ))
        .registerPlugins();

  }

}
