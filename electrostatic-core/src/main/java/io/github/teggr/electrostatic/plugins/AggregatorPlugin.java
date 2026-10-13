package io.github.teggr.electrostatic.plugins;

import io.github.teggr.electrostatic.engine.ContentItem;
import io.github.teggr.electrostatic.engine.ContentModelVisitor;

public interface AggregatorPlugin {

  void visit(ContentModelVisitor visitor);

  void add(ContentItem contentItem);

}
