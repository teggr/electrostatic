package site.electrostatic.plugins;

import site.electrostatic.engine.ContentItem;
import site.electrostatic.engine.ContentModelVisitor;

public interface AggregatorPlugin {

  void visit(ContentModelVisitor visitor);

  void add(ContentItem contentItem);

}
