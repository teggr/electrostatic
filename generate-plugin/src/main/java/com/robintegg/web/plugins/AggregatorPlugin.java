package com.robintegg.web.plugins;

import com.robintegg.web.engine.ContentItem;
import com.robintegg.web.engine.ContentModelVisitor;

public interface AggregatorPlugin {

  void visit(ContentModelVisitor visitor);

  void add(ContentItem contentItem);

}
