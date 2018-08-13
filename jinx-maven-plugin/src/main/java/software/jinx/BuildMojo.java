package software.jinx;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @see https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-tools/spring-boot-maven-plugin/src/main/java/org/springframework/boot/maven/AbstractRunMojo.java
 * 
 *
 */
@Mojo(name = "build", requiresProject = true)
public class BuildMojo extends AbstractMojo {

	private static final String SPRING_BOOT_APPLICATION_CLASS_NAME = "org.springframework.boot.autoconfigure.SpringBootApplication";

	/**
	 * The greeting to display.
	 */
	@Parameter(property = "jinx.main-class", defaultValue = "")
	private String mainClass;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// TODO: not going to be using spring boot directly. autoconfiguration
		// maybe
		// what are my inputs into the jinx engine. we are using spring auto
		// configuration where possible

		// assume convention for the timebeing

	}

}