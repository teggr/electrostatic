package run.electrostatic.maven;

import run.electrostatic.core.SitePreviewServer;
import run.electrostatic.theme.DefaultThemePlugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Path;

/**
 * Generates and serves a static site locally.
 */
@Mojo(
    name = "serve",
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class ServeMojo extends AbstractMojo {

    /**
     * Directory containing the site source content (site-config.xml and content folders).
     */
    @Parameter(
        defaultValue = "${project.basedir}/src/main/resources/site",
        property = "electrostatic.inputDirectory"
    )
    private File inputDirectory;

    /**
     * Directory where the generated site will be written.
     */
    @Parameter(
        defaultValue = "${project.build.directory}/generated-site",
        property = "electrostatic.outputDirectory"
    )
    private File outputDirectory;

    /**
     * Override the base URL defined in site-config.xml.
     */
    @Parameter(property = "electrostatic.baseUrl")
    private String baseUrl;

    /**
     * Port to serve on.
     */
    @Parameter(defaultValue = "8080", property = "electrostatic.port")
    private int port;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        Path projectRoot = project.getBasedir().toPath().toAbsolutePath().normalize();
        Path input = inputDirectory.toPath().toAbsolutePath().normalize();
        Path siteDirectory = outputDirectory.toPath().toAbsolutePath().normalize();

        getLog().info("Generating site from: " + input);
        getLog().info("Output directory: " + siteDirectory);

        try {
            SitePreviewServer.PreviewSession session = new SitePreviewServer(DefaultThemePlugin.create())
                .start(input, siteDirectory, baseUrl, port, projectRoot);
            Runtime.getRuntime().addShutdownHook(new Thread(session::close));

            getLog().info("Serving site from: " + siteDirectory);
            getLog().info("Listening on http://localhost:" + port);
            getLog().info("Press Ctrl+C to stop.");

            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MojoExecutionException("Failed while serving static site", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to serve static site", e);
        }
    }
}