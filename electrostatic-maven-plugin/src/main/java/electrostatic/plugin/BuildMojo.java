package electrostatic.plugin;

import electrostatic.build.BuildContext;
import electrostatic.theme.home.HomeThemePlugin;
import electrostatic.website.WebSiteBuilder;
import electrostatic.website.WebSiteConfigurer;
import electrostatic.website.WebsiteConfiguration;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.SneakyThrows;
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
import java.util.ArrayList;
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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("electrostatic building");

        String wd = project.getBasedir().getAbsolutePath();

        getLog().info("drafts:      " + drafts);
        getLog().info("wd:          " + wd);
        getLog().info("environment: " + environment);
        getLog().info("basePackage: " + basePackage);

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

        BuildContext buildContext = BuildContext.builder()
                .workingDirectory(wd)
                .environment(environment)
                .drafts(drafts)
                .build();

        try {

            // set configuration + themePlugin

            // do we have any configurators (java class or file based configuration), otherwise use the default
            WebSiteConfigurer webSiteConfigurer = findConfigurerOrUseDefault();
            if (webSiteConfigurer == null) {
                throw new RuntimeException("No site configurations found");
            }

            WebsiteConfiguration.WebsiteConfigurationBuilder websiteConfigurationBuilder = WebsiteConfiguration.builder();

            WebsiteConfiguration websiteConfiguration = webSiteConfigurer.configure(websiteConfigurationBuilder);

            WebSiteBuilder webSiteBuilder = new WebSiteBuilder(websiteConfiguration, buildContext);
            webSiteBuilder.build();

        } catch (Exception e) {

            getLog().error("electrostatic failed to complete build", e);
            throw e;

        }

    }

    @SneakyThrows
    private WebSiteConfigurer findConfigurerOrUseDefault() {

        getLog().info("searching package for configuration " + basePackage);

        List<WebSiteConfigurer> candidates = new ArrayList<>();

        try (ScanResult scanResult =
                     new ClassGraph()
                             //.verbose()               // Log to stderr
                             .enableAllInfo()         // Scan classes, methods, fields, annotations
                             .acceptPackages(basePackage)     // Scan com.xyz and subpackages (omit to scan all packages)
                             .scan()) {               // Start the scan
            for (ClassInfo routeClassInfo : scanResult.getClassesImplementing(WebSiteConfigurer.class.getName())) {

                getLog().info("found a website configuration here " + routeClassInfo.getName());

                candidates.add((WebSiteConfigurer) routeClassInfo.loadClass().getDeclaredConstructor().newInstance());

            }
        }

        if (candidates.isEmpty()) {

            return (configuration) -> {
                getLog().info("using default themePlugin");
                configuration.
                        themePlugin(HomeThemePlugin.create());
                return configuration.build();
            };

        } else {

            return (WebSiteConfigurer) candidates.get(0);

        }

    }

}
