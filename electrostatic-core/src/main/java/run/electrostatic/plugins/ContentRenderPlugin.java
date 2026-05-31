package run.electrostatic.plugins;

import run.electrostatic.engine.Layout;

import java.util.Map;

public interface ContentRenderPlugin {

  void loadLayout(Map<String, Layout> layouts);

}
