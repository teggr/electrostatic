package com.robintegg.web.theme.v2;

import com.robintegg.web.content.book.BookPlugin;
import com.robintegg.web.content.feed.FeedSubscriptionPlugin;
import com.robintegg.web.content.podcast.PodcastPlugin;
import com.robintegg.web.content.post.DraftPostPlugin;
import com.robintegg.web.content.post.PostPlugin;
import com.robintegg.web.content.staticfiles.ClasspathFilesPlugin;
import com.robintegg.web.content.staticfiles.StaticFilesPlugin;
import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.Layout;
import com.robintegg.web.engine.Pageable;
import com.robintegg.web.feed.FeedPlugin;
import com.robintegg.web.github.GithubActivityPlugin;
import com.robintegg.web.index.IndexPlugin;
import com.robintegg.web.plugins.ContentRenderPlugin;
import com.robintegg.web.plugins.ContentTypePlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.plugins.ThemePlugin;
import com.robintegg.web.posts.PostsPlugin;
import com.robintegg.web.site.Site;
import com.robintegg.web.tags.TagPlugin;
import com.robintegg.web.theme.layouts.BookLayout;
import com.robintegg.web.theme.layouts.CategoryLayout;
import com.robintegg.web.theme.layouts.PageLayout;
import com.robintegg.web.theme.layouts.PodcastLayout;
import com.robintegg.web.theme.layouts.PostsLayout;
import com.robintegg.web.theme.layouts.TagLayout;
import com.robintegg.web.theme.pages.BooksPage;
import com.robintegg.web.theme.pages.FeedsPage;
import com.robintegg.web.theme.pages.PodcastsPage;
import com.robintegg.web.theme.pages.PostsPage;
import com.robintegg.web.theme.pages.TagsPage;
import com.robintegg.web.theme.pages._404Page;
import com.robintegg.web.theme.v2.layouts.V2DefaultLayout;
import com.robintegg.web.theme.v2.layouts.V2HomeLayout;
import com.robintegg.web.theme.v2.layouts.V2PostLayout;

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
