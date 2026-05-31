package run.electrostatic.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import run.electrostatic.theme.ThemePlugins;

/**
 * Lists available built-in theme bundles.
 */
@Mojo(name = "themes")
public class ThemesMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Available Electrostatic themes:");
        ThemePlugins.availableThemeIds().forEach(themeId -> getLog().info("- " + themeId));
    }
}