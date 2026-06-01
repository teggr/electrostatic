package site.electrostatic.plugins;

import java.nio.file.Path;

public interface InitializationPlugin {

  void initialize(Path sourceDirectory);

}