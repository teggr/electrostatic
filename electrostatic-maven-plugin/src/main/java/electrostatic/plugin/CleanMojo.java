package electrostatic.plugin;

import electrostatic.build.BuildContext;
import electrostatic.build.ElectroStaticSiteBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.util.List;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.NONE)
public class CleanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("electrostatic clean");

        String wd = project.getBasedir().getAbsolutePath();

        getLog().info("wd:          " + wd);

        ElectroStaticSiteBuilder.clean(wd);

    }

}
