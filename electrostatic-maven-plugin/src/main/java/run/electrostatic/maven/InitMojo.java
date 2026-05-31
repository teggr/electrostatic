package run.electrostatic.maven;

import run.electrostatic.core.SiteInitializer;
import run.electrostatic.theme.ThemePlugins;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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
        defaultValue = "${project.basedir}/src/main/resources/site",
        property = "electrostatic.rootDirectory"
    )
    private File rootDirectory;

    @Parameter(defaultValue = "default", property = "electrostatic.theme")
    private String theme;

    @Override
    public void execute() throws MojoExecutionException {
        Path targetDirectory = rootDirectory.toPath().toAbsolutePath().normalize();
        getLog().info("Initializing Electrostatic site at: " + targetDirectory);

        try {
            if (Files.exists(targetDirectory)) {
                throw new MojoExecutionException("Site directory already exists: " + targetDirectory);
            }

            new SiteInitializer(ThemePlugins.resolve(theme)).initialize(targetDirectory);
            getLog().info("Initialization complete.");
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to initialize static site", e);
        }
    }
}