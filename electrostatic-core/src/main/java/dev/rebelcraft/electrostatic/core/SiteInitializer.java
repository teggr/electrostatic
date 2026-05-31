package dev.rebelcraft.electrostatic.core;

import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.plugins.ThemePlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Shared facade for initializing a new Electrostatic site directory.
 * Used by both the CLI and the Maven plugin to keep initialization logic in one place.
 */
public class SiteInitializer {

    private final ThemePlugin themePlugin;

    public SiteInitializer(ThemePlugin themePlugin) {
        this.themePlugin = themePlugin;
    }

    /**
     * Initializes a site in the provided root directory.
     *
     * @param rootDirectory directory to initialize
     */
    public void initialize(Path rootDirectory) {
        Path root = rootDirectory.toAbsolutePath().normalize();
        ensureDirectoryIsEmpty(root);

        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();

        themePlugin.registerPlugins();
        Plugins.initializationPlugins.forEach(plugin -> plugin.initialize(root));
    }

    private void ensureDirectoryIsEmpty(Path root) {
        try {
            if (Files.exists(root) && !Files.isDirectory(root)) {
                throw new IllegalStateException("Target path is not a directory: " + root);
            }

            if (Files.notExists(root)) {
                Files.createDirectories(root);
                return;
            }

            try (Stream<Path> entries = Files.list(root)) {
                if (entries.findAny().isPresent()) {
                    throw new IllegalStateException("Target directory is not empty: " + root);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to prepare target directory: " + root, e);
        }
    }
}