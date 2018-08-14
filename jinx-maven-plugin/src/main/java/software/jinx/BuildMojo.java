package software.jinx;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 * @see https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-tools/spring-boot-maven-plugin/src/main/java/org/springframework/boot/maven/AbstractRunMojo.java
 * 
 *
 */
@Mojo(name = "build", requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST)
public class BuildMojo extends AbstractMojo {

	/**
	 * The enclosing project.
	 */
	@Parameter(property = "project", defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	@Parameter(property = "jinx.baseDir", defaultValue = "${project.basedir}/src/main/resources")
	private File baseDir;

	@Parameter(property = "jinx.outputDir", defaultValue = "${project.build.directory}/site")
	private File outputDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// TODO: not going to be using spring boot directly. autoconfiguration
		// maybe
		// what are my inputs into the jinx engine. we are using spring auto
		// configuration where possible

		getLog().info("baseDir " + baseDir);
		getLog().info("ouputDir " + outputDir);

		SiteGeneratorConfiguration configuration = SiteGeneratorConfiguration.builder().baseDirectory(baseDir)
				.outputDirectory(outputDir).build();

		FileTemplateResolver templateResolver = new FileTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(true);
		templateResolver.setPrefix(new File(baseDir, "templates").getAbsolutePath() + File.separator);
		templateResolver.setSuffix(".html");

		TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(templateResolver);
		engine.setDialect(new SpringStandardDialect());

		Thread.currentThread().setContextClassLoader(getClassLoader());

		Accounts accounts = Accounts.builder().twitter(new TwitterAccount("jinxsoftware"))
				.githubProject(new GithubProjectAccount("teggr", "jinx")).build();

		// how do i get the spring context loaded?
		try {
			ConfigurableApplicationContext applicationContext = SpringApplication
					.run(Class.forName("software.jinx.site.JinxSoftwareSiteApplication"), new String[] {});

			new SiteGenerator(configuration, engine, accounts).run();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoFailureException("Could not generate site", e);
		}

	}

	private ClassLoader getClassLoader() throws MojoExecutionException {
		List<URL> classpathURLs = new ArrayList<URL>();
		addRelevantPluginDependenciesToClasspath(classpathURLs);
		addRelevantProjectDependenciesToClasspath(classpathURLs);
		return new URLClassLoader(classpathURLs.toArray(new URL[classpathURLs.size()]));
	}

	/**
	 * Add any relevant project dependencies to the classpath. Indirectly takes
	 * includePluginDependencies and ExecutableDependency into consideration.
	 * 
	 * @param path classpath of {@link java.net.URL} objects
	 * @throws MojoExecutionException if a problem happens
	 */
	private void addRelevantPluginDependenciesToClasspath(List<URL> path) throws MojoExecutionException {

		try {
			for (Artifact classPathElement : this.determineRelevantPluginDependencies()) {
				getLog().debug(
						"Adding plugin dependency artifact: " + classPathElement.getArtifactId() + " to classpath");
				path.add(classPathElement.getFile().toURI().toURL());
			}
		} catch (MalformedURLException e) {
			throw new MojoExecutionException("Error during setting up classpath", e);
		}

	}

	/**
	 * Add any relevant project dependencies to the classpath. Takes
	 * includeProjectDependencies into consideration.
	 * 
	 * @param path classpath of {@link java.net.URL} objects
	 * @throws MojoExecutionException if a problem happens
	 */
	private void addRelevantProjectDependenciesToClasspath(List<URL> path) throws MojoExecutionException {

		try {
			getLog().debug("Project Dependencies will be included.");

			List<Artifact> artifacts = new ArrayList<Artifact>();
			List<File> theClasspathFiles = new ArrayList<File>();

			collectProjectArtifactsAndClasspath(artifacts, theClasspathFiles);

			for (File classpathFile : theClasspathFiles) {
				URL url = classpathFile.toURI().toURL();
				getLog().debug("Adding to classpath : " + url);
				path.add(url);
			}

			for (Artifact classPathElement : artifacts) {
				getLog().debug(
						"Adding project dependency artifact: " + classPathElement.getArtifactId() + " to classpath");
				path.add(classPathElement.getFile().toURI().toURL());
			}

		} catch (MalformedURLException e) {
			throw new MojoExecutionException("Error during setting up classpath", e);
		}

	}

	/**
	 * Collects the project artifacts in the specified List and the project specific
	 * classpath (build output and build test output) Files in the specified List,
	 * depending on the plugin classpathScope value.
	 * 
	 * @param artifacts         the list where to collect the scope specific
	 *                          artifacts
	 * @param theClasspathFiles the list where to collect the scope specific output
	 *                          directories
	 */
	@SuppressWarnings("unchecked")
	protected void collectProjectArtifactsAndClasspath(List<Artifact> artifacts, List<File> theClasspathFiles) {

		for (Resource r : project.getBuild().getResources()) {
			theClasspathFiles.add(new File(r.getDirectory()));
		}

		artifacts.addAll(project.getRuntimeArtifacts());
		theClasspathFiles.add(new File(project.getBuild().getOutputDirectory()));

		getLog().debug("Collected project artifacts " + artifacts);
		getLog().debug("Collected project classpath " + theClasspathFiles);
	}

	/**
	 * Determine all plugin dependencies relevant to the executable. Takes
	 * includePlugins, and the executableDependency into consideration.
	 * 
	 * @return a set of Artifact objects. (Empty set is returned if there are no
	 *         relevant plugin dependencies.)
	 * @throws MojoExecutionException if a problem happens resolving the plufin
	 *                                dependencies
	 */
	private Set<Artifact> determineRelevantPluginDependencies() throws MojoExecutionException {
		Set<Artifact> relevantDependencies;
//        if ( false )
//        {
//            if ( this.executableDependency == null )
//            {
//                getLog().debug( "All Plugin Dependencies will be included." );
//                relevantDependencies = new HashSet<Artifact>( this.pluginDependencies );
//            }
//            else
//            {
//                getLog().debug( "Selected plugin Dependencies will be included." );
//                Artifact executableArtifact = this.findExecutableArtifact();
//                Artifact executablePomArtifact = this.getExecutablePomArtifact( executableArtifact );
//                relevantDependencies = this.resolveExecutableDependencies( executablePomArtifact );
//            }
//        }
//        else
//        {
		relevantDependencies = Collections.emptySet();
		getLog().debug("Plugin Dependencies will be excluded.");
//        }
		return relevantDependencies;
	}

}