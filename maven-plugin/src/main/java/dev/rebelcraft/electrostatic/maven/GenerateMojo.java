package dev.rebelcraft.electrostatic.maven;

import com.robintegg.web.theme.DefaultThemePlugin;
import dev.rebelcraft.electrostatic.core.SiteGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Generates a static site from the project's site content.
 */
@Mojo(
    name = "generate",
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class GenerateMojo extends AbstractMojo {

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
        defaultValue = "${project.build.directory}/site",
        property = "electrostatic.outputDirectory"
    )
    private File outputDirectory;

    /**
     * Override the base URL defined in site-config.xml.
     */
    @Parameter(property = "electrostatic.baseUrl")
    private String baseUrl;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    private List<String> compileClasspathElements;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating site from: " + inputDirectory);
        getLog().info("Output directory: " + outputDirectory);

        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(buildClassLoader(originalClassLoader));
            new SiteGenerator(DefaultThemePlugin.create())
                .generate(inputDirectory.toPath(), outputDirectory.toPath(), baseUrl);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate static site", e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private ClassLoader buildClassLoader(ClassLoader parent) throws MojoExecutionException {
        try {
            URL[] urls = new URL[compileClasspathElements.size()];
            for (int index = 0; index < compileClasspathElements.size(); index++) {
                urls[index] = new File(compileClasspathElements.get(index)).toURI().toURL();
            }
            return new URLClassLoader(urls, parent);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to build classpath for site generation", e);
        }
    }

}
