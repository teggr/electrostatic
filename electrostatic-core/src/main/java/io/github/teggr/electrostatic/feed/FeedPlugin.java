package io.github.teggr.electrostatic.feed;

import io.github.teggr.electrostatic.content.IndexedContent;
import io.github.teggr.electrostatic.content.staticfiles.StaticFile;
import io.github.teggr.electrostatic.engine.ContentItem;
import io.github.teggr.electrostatic.engine.ContentModelVisitor;
import io.github.teggr.electrostatic.plugins.AggregatorPlugin;
import io.github.teggr.electrostatic.plugins.Plugins;

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
