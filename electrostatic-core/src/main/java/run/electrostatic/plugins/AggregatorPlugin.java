package run.electrostatic.plugins;

import run.electrostatic.engine.ContentItem;
import run.electrostatic.engine.ContentModelVisitor;

public interface AggregatorPlugin {

  void visit(ContentModelVisitor visitor);

  void add(ContentItem contentItem);

}
