package run.electrostatic.feed;

import run.electrostatic.content.IndexedContent;
import run.electrostatic.content.staticfiles.StaticFile;
import run.electrostatic.engine.ContentItem;
import run.electrostatic.engine.ContentModelVisitor;
import run.electrostatic.plugins.AggregatorPlugin;
import run.electrostatic.plugins.Plugins;

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
