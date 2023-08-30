package electrostatic.plugin;

import electrostatic.build.ElectroStaticSiteBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "init", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class InitMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = false, readonly = true)
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("initialising electrostatic project");

        String wd = project.getBasedir().getAbsolutePath();

        getLog().info("wd:          " + wd);

    }

}
