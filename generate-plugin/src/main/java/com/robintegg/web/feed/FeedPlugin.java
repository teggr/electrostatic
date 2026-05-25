package com.robintegg.web.feed;

import com.robintegg.web.content.IndexedContent;
import com.robintegg.web.content.staticfiles.StaticFile;
import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.ContentModelVisitor;
import com.robintegg.web.plugins.AggregatorPlugin;
import com.robintegg.web.plugins.Plugins;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FeedPlugin implements AggregatorPlugin {

  public static final FeedPlugin INSTANCE = new FeedPlugin();

  public static FeedPlugin create() {
    return INSTANCE;
  }

  private Feed feed = new Feed();

  @Override
  public void visit(ContentModelVisitor visitor) {

    // feed
    StaticFile staticFile = new StaticFile(
        this.feed.getPath(),
        Map.of(),
        contentModel -> this.feed.getContent(contentModel)
            .getBytes(StandardCharsets.UTF_8)
    );

    visitor.file(staticFile);

  }

  @Override
  public void add(ContentItem contentItem) {
    if (contentItem instanceof IndexedContent ic) {
      this.feed.addContent(ic.getIndexContent());
    }
  }

  public Feed getFeed() {
    return feed;
  }

  public void registerPlugins() {
    Plugins.aggregatorPlugins.add(this);
  }
}
