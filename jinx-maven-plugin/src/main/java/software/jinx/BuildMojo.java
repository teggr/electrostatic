package software.jinx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.collection.AbstractArtifactFeatureFilter;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import org.springframework.boot.loader.tools.MainClassFinder;
import org.springframework.boot.maven.MatchingGroupIdFilter;
import org.springframework.boot.maven.RunArguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 * @see https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-tools/spring-boot-maven-plugin/src/main/java/org/springframework/boot/maven/AbstractRunMojo.java
 * 
 *
 */
@Mojo(name = "build", requiresProject = true, defaultPhase = LifecyclePhase.VALIDATE, requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase=LifecyclePhase.TEST_COMPILE)
public class BuildMojo extends AbstractMojo {

	private static final String SPRING_BOOT_APPLICATION_CLASS_NAME = "org.springframework.boot.autoconfigure.SpringBootApplication";
	
	/**
	 * The enclosing project.
	 */
	@Parameter(property = "project", defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	@Parameter(property = "jinx.baseDir", defaultValue = "${project.basedir}/src/main/resources")
	private File baseDir;

	@Parameter(property = "jinx.outputDir", defaultValue = "${project.build.directory}/site")
	private File outputDir;

	/**
	 * Directory containing the classes and resource files that should be
	 * packaged into the archive.
	 * 
	 * @since 1.0
	 */
	@Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
	private File classesDirectory;

	private boolean useTestClasspath = false;

	private String[] arguments;

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

		Accounts accounts = Accounts.builder().twitter(new TwitterAccount()).githubProject(new GithubProjectAccount())
				.build();

		runWithMavenJvm(getStartClass(),
				resolveApplicationArguments().asArray());

		//
		//
		// // how do i get the spring context loaded?
		// try {
		// ConfigurableApplicationContext applicationContext = SpringApplication
		// .run(Class.forName(), new String[] {});
		//
		// new SiteGenerator(configuration, engine, accounts).run();
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw new MojoFailureException("Could not generate site", e);
		// }

	}
	
	/**
	 * Resolve the application arguments to use.
	 * @return a {@link RunArguments} defining the application arguments
	 */
	protected RunArguments resolveApplicationArguments() {
		RunArguments runArguments = new RunArguments(this.arguments);
		addActiveProfileArgument(runArguments);
		runArguments.getArgs().add("--spring.main.web-application-type=none"); // ensures we don't start up a server
		return runArguments;
}
	
	private void addActiveProfileArgument(RunArguments arguments) {
		// if (this.profiles.length > 0) {
		// StringBuilder arg = new StringBuilder("--spring.profiles.active=");
		// for (int i = 0; i < this.profiles.length; i++) {
		// arg.append(this.profiles[i]);
		// if (i < this.profiles.length - 1) {
		// arg.append(",");
		// }
		// }
		// arguments.getArgs().addFirst(arg.toString());
		// logArguments("Active profile(s): ", this.profiles);
		// }
}
	
	private String getStartClass() throws MojoExecutionException {
		String mainClass = null;
		if (mainClass == null) {
			try {
				mainClass = MainClassFinder.findSingleMainClass(this.classesDirectory,
						SPRING_BOOT_APPLICATION_CLASS_NAME);
			}
			catch (IOException ex) {
				throw new MojoExecutionException(ex.getMessage(), ex);
			}
		}
		if (mainClass == null) {
			throw new MojoExecutionException("Unable to find a suitable main class, "
					+ "please add a 'mainClass' property");
		}
		return mainClass;
}

	protected void runWithMavenJvm(String startClassName, String... arguments) throws MojoExecutionException {
		IsolatedThreadGroup threadGroup = new IsolatedThreadGroup(startClassName);
		Thread launchThread = new Thread(threadGroup, new LaunchRunner(startClassName, arguments), "main");
		launchThread.setContextClassLoader(new URLClassLoader(getClassPathUrls()));
		launchThread.start();
		join(threadGroup);
		threadGroup.rethrowUncaughtException();
	}

	private void join(ThreadGroup threadGroup) {
		boolean hasNonDaemonThreads;
		do {
			hasNonDaemonThreads = false;
			Thread[] threads = new Thread[threadGroup.activeCount()];
			threadGroup.enumerate(threads);
			for (Thread thread : threads) {
				if (thread != null && !thread.isDaemon()) {
					try {
						hasNonDaemonThreads = true;
						thread.join();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		} while (hasNonDaemonThreads);
	}

	protected URL[] getClassPathUrls() throws MojoExecutionException {
		try {
			List<URL> urls = new ArrayList<>();
			addUserDefinedFolders(urls);
			addResources(urls);
			addProjectClasses(urls);
			addDependencies(urls);
			return urls.toArray(new URL[0]);
		} catch (IOException ex) {
			throw new MojoExecutionException("Unable to build classpath", ex);
		}
	}

	private void addUserDefinedFolders(List<URL> urls) throws MalformedURLException {
		// if (this.folders != null) {
		// for (String folder : this.folders) {
		// urls.add(new File(folder).toURI().toURL());
		// }
		// }
	}

	private void addResources(List<URL> urls) throws IOException {
		// if (this.addResources) {
		// for (Resource resource : this.project.getResources()) {
		// File directory = new File(resource.getDirectory());
		// urls.add(directory.toURI().toURL());
		// FileUtils.removeDuplicatesFromOutputDirectory(this.classesDirectory,
		// directory);
		// }
		// }
	}

	private void addProjectClasses(List<URL> urls) throws MalformedURLException {
		urls.add(this.classesDirectory.toURI().toURL());
	}

	private void addDependencies(List<URL> urls) throws MalformedURLException, MojoExecutionException {
		FilterArtifacts filters = (this.useTestClasspath ? getFilters() : getFilters(new TestArtifactFilter()));
		Set<Artifact> artifacts = filterDependencies(this.project.getArtifacts(), filters);
		for (Artifact artifact : artifacts) {
			if (artifact.getFile() != null) {
				urls.add(artifact.getFile().toURI().toURL());
			}
		}
	}

	protected Set<Artifact> filterDependencies(Set<Artifact> dependencies, FilterArtifacts filters)
			throws MojoExecutionException {
		try {
			Set<Artifact> filtered = new LinkedHashSet<>(dependencies);
			filtered.retainAll(filters.filter(dependencies));
			return filtered;
		} catch (ArtifactFilterException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}

	}

	/**
	 * Return artifact filters configured for this MOJO.
	 * 
	 * @param additionalFilters
	 *            optional additional filters to apply
	 * @return the filters
	 */
	protected final FilterArtifacts getFilters(ArtifactsFilter... additionalFilters) {
		FilterArtifacts filters = new FilterArtifacts();
		for (ArtifactsFilter additionalFilter : additionalFilters) {
			filters.addFilter(additionalFilter);
		}
		filters.addFilter(new MatchingGroupIdFilter(cleanFilterConfig("")));
		// if (this.includes != null && !this.includes.isEmpty()) {
		// filters.addFilter(new IncludeFilter(this.includes));
		// }
		// if (this.excludes != null && !this.excludes.isEmpty()) {
		// filters.addFilter(new ExcludeFilter(this.excludes));
		// }
		return filters;
	}

	private String cleanFilterConfig(String content) {
		if (content == null || content.trim().isEmpty()) {
			return "";
		}
		StringBuilder cleaned = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(content, ",");
		while (tokenizer.hasMoreElements()) {
			cleaned.append(tokenizer.nextToken().trim());
			if (tokenizer.hasMoreElements()) {
				cleaned.append(",");
			}
		}
		return cleaned.toString();
	}

	private static class TestArtifactFilter extends AbstractArtifactFeatureFilter {

		TestArtifactFilter() {
			super("", Artifact.SCOPE_TEST);
		}

		@Override
		protected String getArtifactFeature(Artifact artifact) {
			return artifact.getScope();
		}

	}

	/**
	 * Isolated {@link ThreadGroup} to capture uncaught exceptions.
	 */
	class IsolatedThreadGroup extends ThreadGroup {

		private final Object monitor = new Object();

		private Throwable exception;

		IsolatedThreadGroup(String name) {
			super(name);
		}

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			if (!(ex instanceof ThreadDeath)) {
				synchronized (this.monitor) {
					this.exception = (this.exception != null) ? this.exception : ex;
				}
				getLog().warn(ex);
			}
		}

		public void rethrowUncaughtException() throws MojoExecutionException {
			synchronized (this.monitor) {
				if (this.exception != null) {
					throw new MojoExecutionException(
							"An exception occurred while running. " + this.exception.getMessage(), this.exception);
				}
			}
		}

	}

	/**
	 * Runner used to launch the application.
	 */
	class LaunchRunner implements Runnable {

		private final String startClassName;

		private final String[] args;

		LaunchRunner(String startClassName, String... args) {
			this.startClassName = startClassName;
			this.args = (args != null) ? args : new String[] {};
		}

		@Override
		public void run() {
			Thread thread = Thread.currentThread();
			ClassLoader classLoader = thread.getContextClassLoader();
			try {
				Class<?> startClass = classLoader.loadClass(this.startClassName);
				Method mainMethod = startClass.getMethod("main", String[].class);
				if (!mainMethod.isAccessible()) {
					mainMethod.setAccessible(true);
				}
				mainMethod.invoke(null, new Object[] { this.args });				
			} catch (NoSuchMethodException ex) {
				Exception wrappedEx = new Exception(
						"The specified mainClass doesn't contain a " + "main method with appropriate signature.", ex);
				thread.getThreadGroup().uncaughtException(thread, wrappedEx);
			} catch (Exception ex) {
				thread.getThreadGroup().uncaughtException(thread, ex);
			}
		}

	}

}