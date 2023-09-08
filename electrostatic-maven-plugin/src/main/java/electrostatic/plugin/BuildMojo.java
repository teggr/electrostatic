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

@Mojo(name = "build", defaultPhase = LifecyclePhase.NONE)
public class BuildMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Component
    PluginDescriptor pluginDescriptor;

    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    List<String> classpath;

    @Parameter(property = "environment", defaultValue = "local")
    String environment;

    @Parameter(property = "drafts", defaultValue = "true")
    boolean drafts;

    @Parameter(property = "basePackage", defaultValue = "${project.groupId}")
    String basePackage;

    @Parameter(property = "theme", defaultValue = "home")
    String theme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("electrostatic building");

        String wd = project.getBasedir().getAbsolutePath();

        getLog().info("drafts:      " + drafts);
        getLog().info("wd:          " + wd);
        getLog().info("environment: " + environment);
        getLog().info("basePackage: " + basePackage);

        addProjectClasspathToPlugin();

        BuildContext build = BuildContext.builder()
                .workingDirectory(wd)
                .basePackage(basePackage)
                .environment(environment)
                .drafts(drafts)
                .themeName(theme)
                .build();

        ElectroStaticSiteBuilder.builder()
                .buildContext(build)
                .build()
                .build();

    }

    private void addProjectClasspathToPlugin() {
        // need to configure the plugins here
        classpath.forEach(cp -> {
            getLog().info("cp:          " + cp);
            try {

                URL url = new File(cp).toURI().toURL();

                pluginDescriptor.getClassRealm().addURL(url);

            } catch (Exception e) {
                getLog().error("electrostatic failed to complete build", e);
                throw new RuntimeException(e);
            }
        });
    }

}
