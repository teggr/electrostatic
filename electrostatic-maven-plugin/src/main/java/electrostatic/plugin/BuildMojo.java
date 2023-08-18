package electrostatic.plugin;

import electrostatic.engine.WebSiteBuilder;
import electrostatic.theme.DefaultThemePlugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "build", defaultPhase = LifecyclePhase.NONE)
public class BuildMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "environment", defaultValue = "local")
    String environment;

    @Parameter(property = "drafts", defaultValue = "true")
    boolean drafts;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("electrostatic building");

        String wd = project.getBasedir().getAbsolutePath();

        getLog().info("drafts:      " + drafts);
        getLog().info("wd:          " + wd);
        getLog().info("environment: " + environment);

        // need to configure the plugins here

        try {

            // set configuration + theme
            WebSiteBuilder webSiteBuilder = new WebSiteBuilder(
                    DefaultThemePlugin.create(wd, environment, drafts)
            );

            webSiteBuilder.build(wd, environment);

        } catch (Exception e) {

            getLog().error("electrostatic failed to complete build", e);
            throw e;

        }

    }

}
