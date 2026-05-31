package run.electrostatic.core;

import run.electrostatic.engine.WebSiteBuilder;
import run.electrostatic.plugins.ThemePlugin;

import java.nio.file.Path;

/**
 * Shared facade for invoking the Electrostatic static site generator.
 * Used by both the CLI and the Maven plugin to keep generation logic in one place
 * while keeping their respective framework dependencies separate.
 */
public class SiteGenerator {

    private final ThemePlugin themePlugin;

    public SiteGenerator(ThemePlugin themePlugin) {
        this.themePlugin = themePlugin;
    }

    /**
     * Generates the static site.
     *
     * @param inputDirectory  directory containing site source content (site-config.xml and content folders)
     * @param outputDirectory directory where the generated site will be written
     * @param baseUrl         optional base URL override; {@code null} keeps the value from site-config.xml
     */
    public void generate(Path inputDirectory, Path outputDirectory, String baseUrl) {
        new WebSiteBuilder(themePlugin).build(baseUrl, inputDirectory, outputDirectory);
    }

}