package io.github.teggr.electrostatic.plugins;

import io.github.teggr.electrostatic.engine.Layout;

import java.util.Map;

public interface ContentRenderPlugin {

  void loadLayout(Map<String, Layout> layouts);

}
