package electrostatic.plugins;

import electrostatic.engine.ContentItem;
import electrostatic.engine.ContentModelVisitor;

public interface AggregatorPlugin {

  void visit(ContentModelVisitor visitor);

  void add(ContentItem contentItem);

}
