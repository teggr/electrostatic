package dev.rebelcraft.electrostatic.core;

import com.robintegg.web.engine.WebSiteBuilder;
import com.robintegg.web.plugins.ThemePlugin;

import java.nio.file.Path;

public class SiteGenerator {

    private final ThemePlugin themePlugin;

    public SiteGenerator(ThemePlugin themePlugin) {
        this.themePlugin = themePlugin;
    }

    public void generate(Path inputDirectory, Path outputDirectory, String baseUrl) {
        new WebSiteBuilder(themePlugin).build(baseUrl, inputDirectory, outputDirectory);
    }

}
