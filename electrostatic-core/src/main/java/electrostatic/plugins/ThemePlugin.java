package electrostatic.plugins;

import electrostatic.build.BuildContext;

public interface ThemePlugin {

  void registerPlugins(PluginContext pluginContext, BuildContext buildContext);

}
