package site.electrostatic;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.zeroturnaround.zip.ZipUtil;

@Mojo(name = "zip", requiresProject = true)
public class ZipMojo extends AbstractMojo {
	
	private File sourceDirectory;
	
	private File outputDirectory;
	
	private String zipFileName;
	
	private boolean preserveRoot;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("ZIPPING THE PATOULI OUT OF EVERYTHING");
		
		File root = null;
		File zip = null;
		boolean preserveRoot = false;
		// need source directory and zip name
		ZipUtil.pack(root, zip, preserveRoot);
		
	}

}