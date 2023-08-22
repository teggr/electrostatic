package electrostatic.layouts;

import electrostatic.engine.Layout;
import electrostatic.plugins.ContentRenderPlugin;
import electrostatic.plugins.Plugins;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LayoutsPlugin implements ContentRenderPlugin {

    private final Map<String, Layout> layouts = new HashMap<>();

    @Override
    public void loadLayout(Map<String, Layout> layouts) {
        log.info("loading layouts");
        layouts.putAll(this.layouts);
    }

    public void put(String name, Layout layout) {
        layouts.put(name, layout);
    }

    public void register() {
        Plugins.contentRenderPlugins.add(this);
    }

}
