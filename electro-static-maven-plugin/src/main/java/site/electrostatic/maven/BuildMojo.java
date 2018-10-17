package site.electrostatic.maven;

import java.beans.DesignMode;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;

/**
 * Goal which builds your site
 *
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class BuildMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}/electro", property = "outputDir", required = true)
	private File outputDirectory;

	/**
	 * Location of the static files.
	 */
	@Parameter(defaultValue = "${project.basedir}/src/main/resources/static", property = "staticDir", required = true)
	private File staticDirectory;

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	/**
	 * To look up Archiver/UnArchiver implementations
	 */
	@Component
	private ArchiverManager archiverManager;

	@Component
	private ArtifactResolver artifactResolver;

	/**
	 * The Maven session
	 */
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	protected MavenSession session;

	public void execute() throws MojoExecutionException {

		getLog().info("Copy static directory");
		try {
			FileUtils.copyDirectory(staticDirectory, outputDirectory);
		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Could not build site", e);
		}

		getLog().info("Collect web jars");
		try {
			Set<Artifact> collect = project.getDependencyArtifacts().stream()
					.filter(a -> a.getGroupId().contains("org.webjars")).collect(Collectors.toSet());

			for (Artifact artifact : collect) {
				Artifact resolvedArtifact = resolveArtifact(artifact);
				unpack(resolvedArtifact, outputDirectory);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException("Could not build site", e);
		}

	}

	private Artifact resolveArtifact(Artifact artifact) throws ArtifactResolverException {
		ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
		return artifactResolver.resolveArtifact(buildingRequest, artifact).getArtifact();
	}

	private void unpack(Artifact artifact, File outputDirectory2) throws NoSuchArchiverException, IOException {

		UnArchiver unArchiver;

		File file = artifact.getFile();
		String type = artifact.getType();

		getLog().info("Unpacking: " + file);

		try {
			unArchiver = archiverManager.getUnArchiver(type);
			getLog().info("Found unArchiver by type: " + unArchiver);
		} catch (NoSuchArchiverException e) {
			unArchiver = archiverManager.getUnArchiver(file);
			getLog().info("Found unArchiver by extension: " + unArchiver);
		}

		unArchiver.setSourceFile(file);
		File destDirectory = new File(outputDirectory2.getParent(), "tmp-dependencies/" + artifact.getArtifactId());
		destDirectory.mkdirs();
		unArchiver.setDestDirectory(destDirectory);
		unArchiver.extract();
		FileUtils.copyDirectory(new File(destDirectory, "META-INF/resources"), outputDirectory2);
	}
}
