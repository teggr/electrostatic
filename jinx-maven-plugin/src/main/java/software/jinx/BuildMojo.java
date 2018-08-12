package software.jinx;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "build", requiresProject = true)
public class BuildMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		// TODO: not going to be using spring boot directly. autoconfiguration maybe
		// what are my inputs into the jinx engine. we are using spring auto configuration where possible
		
	}



}
