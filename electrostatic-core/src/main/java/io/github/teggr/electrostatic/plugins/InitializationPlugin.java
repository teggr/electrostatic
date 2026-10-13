package io.github.teggr.electrostatic.plugins;

import java.nio.file.Path;

public interface InitializationPlugin {

  void initialize(Path sourceDirectory);

}