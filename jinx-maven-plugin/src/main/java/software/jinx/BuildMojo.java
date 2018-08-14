package software.jinx;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 * @see https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-tools/spring-boot-maven-plugin/src/main/java/org/springframework/boot/maven/AbstractRunMojo.java
 * 
 *
 */
@Mojo(name = "build", requiresProject = true)
public class BuildMojo extends AbstractMojo {

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

		Accounts accounts = Accounts.builder().twitter(new TwitterAccount("jinxsoftware"))
				.githubProject(new GithubProjectAccount("teggr", "jinx")).build();

		try {
			new SiteGenerator(configuration, engine, accounts).run();
		} catch (Exception e) {
			throw new MojoFailureException("Could not generate site", e);
		}

	}

}