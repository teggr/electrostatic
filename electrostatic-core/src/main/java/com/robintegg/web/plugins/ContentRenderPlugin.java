package com.robintegg.web.plugins;

import com.robintegg.web.engine.Layout;

import java.util.Map;

public interface ContentRenderPlugin {

  void loadLayout(Map<String, Layout> layouts);

}
