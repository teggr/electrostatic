package run.electrostatic.categories;

import run.electrostatic.content.CategorisedContent;
import run.electrostatic.content.IndexContent;
import run.electrostatic.content.IndexedContent;
import run.electrostatic.engine.ContentItem;
import run.electrostatic.engine.ContentModelVisitor;
import run.electrostatic.engine.Page;
import run.electrostatic.plugins.AggregatorPlugin;
import run.electrostatic.plugins.Plugins;
import run.electrostatic.theme.layouts.CategoryLayout;
import run.electrostatic.theme.layouts.TagLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CategoriesPlugin implements AggregatorPlugin {

  public static final CategoriesPlugin INSTANCE = new CategoriesPlugin();

  public static final CategoriesPlugin create() {
    return INSTANCE;
  }

  private Map<String, List<ContentItem>> categorisedContentByCategory = new HashMap<>();

  @Override
  public void visit(ContentModelVisitor visitor) {

    categorisedContentByCategory.entrySet().stream()
        .map(entry -> {
          String category = entry.getKey();
          log.info("category={}", category);
          return Page.builder()
              .data(Map.of(
                  "category", List.of(category)
              ))
              .path("/categories/" + category + "/index.html")
              .renderFunction(CategoryLayout::render)
              .build();
        })
        .forEach(visitor::page);

  }

  @Override
  public void add(ContentItem contentItem) {

    if (contentItem instanceof CategorisedContent cc) {

      String category = cc.getCategory();
      if (category != null) {
        List<ContentItem> list = categorisedContentByCategory.getOrDefault(category, new ArrayList<>());
        list.add(contentItem);
        categorisedContentByCategory.put(category, list);
      }

    }

  }

  public List<String> getCategories() {
    return categorisedContentByCategory.keySet().stream()
        .sorted()
        .toList();
  }

  public List<IndexContent> getPostsInCategory(String category) {
    return categorisedContentByCategory.getOrDefault(category, List.of()).stream()
        .filter(i -> i instanceof IndexedContent)
        .map(i -> ((IndexedContent) i).getIndexContent())
        .sorted(Comparator.comparing(IndexContent::getDate).reversed())
        .toList();
  }

  public void registerPlugins() {
    Plugins.aggregatorPlugins.add(this);
  }

}
