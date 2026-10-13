package io.github.teggr.electrostatic.theme.v2;

import io.github.teggr.electrostatic.content.book.BookPlugin;
import io.github.teggr.electrostatic.content.feed.FeedSubscriptionPlugin;
import io.github.teggr.electrostatic.content.podcast.PodcastPlugin;
import io.github.teggr.electrostatic.content.post.DraftPostPlugin;
import io.github.teggr.electrostatic.content.post.PostPlugin;
import io.github.teggr.electrostatic.content.staticfiles.ClasspathFilesPlugin;
import io.github.teggr.electrostatic.content.staticfiles.StaticFilesPlugin;
import io.github.teggr.electrostatic.engine.ContentModel;
import io.github.teggr.electrostatic.engine.Layout;
import io.github.teggr.electrostatic.engine.Pageable;
import io.github.teggr.electrostatic.feed.FeedPlugin;
import io.github.teggr.electrostatic.github.GithubActivityPlugin;
import io.github.teggr.electrostatic.index.IndexPlugin;
import io.github.teggr.electrostatic.plugins.ContentRenderPlugin;
import io.github.teggr.electrostatic.plugins.ContentTypePlugin;
import io.github.teggr.electrostatic.plugins.Plugins;
import io.github.teggr.electrostatic.plugins.ThemePlugin;
import io.github.teggr.electrostatic.posts.PostsPlugin;
import io.github.teggr.electrostatic.site.Site;
import io.github.teggr.electrostatic.tags.TagPlugin;
import io.github.teggr.electrostatic.theme.layouts.BookLayout;
import io.github.teggr.electrostatic.theme.layouts.CategoryLayout;
import io.github.teggr.electrostatic.theme.layouts.PageLayout;
import io.github.teggr.electrostatic.theme.layouts.PodcastLayout;
import io.github.teggr.electrostatic.theme.layouts.PostsLayout;
import io.github.teggr.electrostatic.theme.layouts.TagLayout;
import io.github.teggr.electrostatic.theme.pages.BooksPage;
import io.github.teggr.electrostatic.theme.pages.FeedsPage;
import io.github.teggr.electrostatic.theme.pages.PodcastsPage;
import io.github.teggr.electrostatic.theme.pages.PostsPage;
import io.github.teggr.electrostatic.theme.pages.TagsPage;
import io.github.teggr.electrostatic.theme.pages._404Page;
import io.github.teggr.electrostatic.theme.v2.layouts.V2DefaultLayout;
import io.github.teggr.electrostatic.theme.v2.layouts.V2HomeLayout;
import io.github.teggr.electrostatic.theme.v2.layouts.V2PostLayout;

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
