package site.electrostatic.plugins;

import site.electrostatic.engine.Layout;

import java.util.Map;

public interface ContentRenderPlugin {

  void loadLayout(Map<String, Layout> layouts);

}
