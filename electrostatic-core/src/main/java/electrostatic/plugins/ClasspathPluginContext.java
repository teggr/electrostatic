package electrostatic.plugins;

import electrostatic.utils.ClasspathScanner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClasspathPluginContext implements PluginContext {

    private final ClasspathScanner classpathScanner;

}
