package run.electrostatic.theme.v2;

import run.electrostatic.content.book.BookPlugin;
import run.electrostatic.content.feed.FeedSubscriptionPlugin;
import run.electrostatic.content.podcast.PodcastPlugin;
import run.electrostatic.content.post.DraftPostPlugin;
import run.electrostatic.content.post.PostPlugin;
import run.electrostatic.content.staticfiles.ClasspathFilesPlugin;
import run.electrostatic.content.staticfiles.StaticFilesPlugin;
import run.electrostatic.engine.ContentModel;
import run.electrostatic.engine.Layout;
import run.electrostatic.engine.Pageable;
import run.electrostatic.feed.FeedPlugin;
import run.electrostatic.github.GithubActivityPlugin;
import run.electrostatic.index.IndexPlugin;
import run.electrostatic.plugins.ContentRenderPlugin;
import run.electrostatic.plugins.ContentTypePlugin;
import run.electrostatic.plugins.Plugins;
import run.electrostatic.plugins.ThemePlugin;
import run.electrostatic.posts.PostsPlugin;
import run.electrostatic.site.Site;
import run.electrostatic.tags.TagPlugin;
import run.electrostatic.theme.layouts.BookLayout;
import run.electrostatic.theme.layouts.CategoryLayout;
import run.electrostatic.theme.layouts.PageLayout;
import run.electrostatic.theme.layouts.PodcastLayout;
import run.electrostatic.theme.layouts.PostsLayout;
import run.electrostatic.theme.layouts.TagLayout;
import run.electrostatic.theme.pages.BooksPage;
import run.electrostatic.theme.pages.FeedsPage;
import run.electrostatic.theme.pages.PodcastsPage;
import run.electrostatic.theme.pages.PostsPage;
import run.electrostatic.theme.pages.TagsPage;
import run.electrostatic.theme.pages._404Page;
import run.electrostatic.theme.v2.layouts.V2DefaultLayout;
import run.electrostatic.theme.v2.layouts.V2HomeLayout;
import run.electrostatic.theme.v2.layouts.V2PostLayout;

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
