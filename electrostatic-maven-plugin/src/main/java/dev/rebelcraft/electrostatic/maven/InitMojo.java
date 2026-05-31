package dev.rebelcraft.electrostatic.maven;

import com.robintegg.web.theme.DefaultThemePlugin;
import dev.rebelcraft.electrostatic.core.SiteInitializer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;

/**
 * Initializes a new static site structure.
 */
@Mojo(
    name = "init",
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class InitMojo extends AbstractMojo {

    /**
     * Directory to initialize with Electrostatic content structure.
     */
    @Parameter(
        defaultValue = "${project.basedir}",
        property = "electrostatic.rootDirectory"
    )
    private File rootDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Initializing Electrostatic site at: " + rootDirectory);

        try {
            new SiteInitializer(DefaultThemePlugin.create()).initialize(rootDirectory.toPath());
            getLog().info("Initialization complete.");
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to initialize static site", e);
        }
    }
}