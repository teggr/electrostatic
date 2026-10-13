package io.github.teggr.electrostatic.tags;

import io.github.teggr.electrostatic.content.IndexContent;
import io.github.teggr.electrostatic.content.IndexedContent;
import io.github.teggr.electrostatic.content.TaggedContent;
import io.github.teggr.electrostatic.engine.ContentItem;
import io.github.teggr.electrostatic.engine.ContentModelVisitor;
import io.github.teggr.electrostatic.engine.Page;
import io.github.teggr.electrostatic.plugins.AggregatorPlugin;
import io.github.teggr.electrostatic.plugins.Plugins;
import io.github.teggr.electrostatic.theme.layouts.TagLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class TagPlugin implements AggregatorPlugin {

  public static final TagPlugin INSTANCE = new TagPlugin();

  public static final TagPlugin create() {
    return INSTANCE;
  }

  private Map<String, List<ContentItem>> taggedContentByTag = new HashMap<>();

  @Override
  public void visit(ContentModelVisitor visitor) {

    taggedContentByTag.entrySet().stream()
        .map(entry -> {
          String tag = entry.getKey();
          log.info("tag={}", tag);
          return Page.builder()
              .data(Map.of(
                  "tag", List.of(tag)
              ))
              .path("/tags/" + tag + "/index.html")
              .renderFunction(TagLayout::render)
              .build();
        })
        .forEach(visitor::page);

  }

  @Override
  public void add(ContentItem contentItem) {

    if (contentItem instanceof TaggedContent tc) {

      tc.getTags().stream()
          .forEach(t -> {
            List<ContentItem> list = taggedContentByTag.getOrDefault(t, new ArrayList<>());
            list.add(contentItem);
            taggedContentByTag.put(t, list);
          });

    }

  }

  public List<String> getTags() {
    return taggedContentByTag.keySet().stream()
        .sorted()
        .toList();
  }

  public List<IndexContent> getTaggedContent(String tag) {
    return taggedContentByTag.getOrDefault(tag, List.of()).stream()
        .filter(i -> i instanceof IndexedContent)
        .map(i -> ((IndexedContent) i).getIndexContent())
        .sorted(Comparator.comparing(IndexContent::getDate).reversed())
        .toList();
  }

  public void registerPlugins() {
    Plugins.aggregatorPlugins.add(this);
  }

}
