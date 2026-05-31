package com.robintegg.web.theme;

import com.robintegg.web.content.book.BookPlugin;
import com.robintegg.web.content.feed.FeedSubscriptionPlugin;
import com.robintegg.web.content.podcast.PodcastPlugin;
import com.robintegg.web.content.post.DraftPostPlugin;
import com.robintegg.web.content.post.PostPlugin;
import com.robintegg.web.content.staticfiles.ClasspathFilesPlugin;
import com.robintegg.web.content.staticfiles.StaticFilesPlugin;
import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.Layout;
import com.robintegg.web.feed.FeedPlugin;
import com.robintegg.web.github.GithubActivityPlugin;
import com.robintegg.web.index.IndexPlugin;
import com.robintegg.web.plugins.ContentRenderPlugin;
import com.robintegg.web.plugins.ContentTypePlugin;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.plugins.ThemePlugin;
import com.robintegg.web.site.Site;
import com.robintegg.web.tags.TagPlugin;
import com.robintegg.web.theme.layouts.*;
import com.robintegg.web.theme.pages.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class DefaultThemePlugin implements ContentTypePlugin, ContentRenderPlugin, ThemePlugin {
  public static DefaultThemePlugin create() {
    return new DefaultThemePlugin();
  }

  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {
    contentModel.addPage(_404Page.create());
    contentModel.addPage(TagsPage.create());
    contentModel.addPage(PodcastsPage.create());
    contentModel.addPage(BooksPage.create());
    contentModel.addPage(FeedsPage.create());
    contentModel.addPage(PostsPage.create("/posts/index.html", new com.robintegg.web.engine.Pageable(1, 10)));
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
    layouts.put("posts", PostsLayout.create());
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
    com.robintegg.web.posts.PostsPlugin.create().registerPlugins();

    // TODO: jsass for sass compiling or move to ph-css or alternative
    ClasspathFilesPlugin.create(List.of(
            "theme/default/css/main.css",
            "theme/default/css/style.css",
            "theme/default/images/minima-social-icons.svg"
        ))
        .registerPlugins();

  }

}
