package run.electrostatic.smoke;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.maven.plugin.MojoExecutionException;
import run.electrostatic.maven.GenerateMojo;
import run.electrostatic.maven.InitMojo;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenPluginSmokeTest {

    @TempDir
    Path tempDir;

    @Test
    void initAndGenerateGoalsShouldCreateExpectedSiteStructure() throws Exception {
        Path siteRoot = tempDir.resolve("plugin-site");
        Path outputDir = siteRoot.resolve("target/generated-site");

        InitMojo initMojo = new InitMojo();
        setField(initMojo, "rootDirectory", siteRoot.toFile());
        initMojo.execute();

        assertTrue(Files.exists(siteRoot.resolve("site-config.xml")));
        assertTrue(Files.exists(siteRoot.resolve("_posts/hello-world.md")));

        rewriteDefaultPostToDatedPost(siteRoot);

        GenerateMojo generateMojo = new GenerateMojo();
        setField(generateMojo, "inputDirectory", siteRoot.toFile());
        setField(generateMojo, "outputDirectory", outputDir.toFile());
        setField(generateMojo, "compileClasspathElements", List.<String>of());
        generateMojo.execute();

        assertTrue(Files.isDirectory(outputDir));
        assertTrue(hasHtmlFile(outputDir));
    }

    @Test
    void initShouldSucceedInExistingMavenModuleWhenSiteDirectoryDoesNotExist() throws Exception {
        Path moduleRoot = tempDir.resolve("existing-module");
        Path siteDirectory = moduleRoot.resolve("src/main/resources/site");
        Files.createDirectories(moduleRoot.resolve("src/main/java"));
        Files.createDirectories(moduleRoot.resolve("target"));
        Files.writeString(moduleRoot.resolve("pom.xml"), "<project/>");

        InitMojo initMojo = new InitMojo();
        setField(initMojo, "rootDirectory", siteDirectory.toFile());
        initMojo.execute();

        assertTrue(Files.exists(siteDirectory.resolve("site-config.xml")));
        assertTrue(Files.exists(siteDirectory.resolve("_posts/hello-world.md")));
    }

    @Test
    void initShouldFailWhenSiteDirectoryAlreadyExists() throws Exception {
        Path moduleRoot = tempDir.resolve("existing-module-with-site");
        Path siteDirectory = moduleRoot.resolve("src/main/resources/site");
        Files.createDirectories(siteDirectory);

        InitMojo initMojo = new InitMojo();
        setField(initMojo, "rootDirectory", siteDirectory.toFile());

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, initMojo::execute);
        assertTrue(exception.getMessage().contains("Site directory already exists"));
    }

    private static boolean hasHtmlFile(Path outputDirectory) throws Exception {
        try (Stream<Path> paths = Files.walk(outputDirectory)) {
            return paths
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .anyMatch(name -> name.endsWith(".html"));
        }
    }

    private static void rewriteDefaultPostToDatedPost(Path siteRoot) throws Exception {
        Path postsDirectory = siteRoot.resolve("_posts");
        Files.deleteIfExists(postsDirectory.resolve("hello-world.md"));
        Files.writeString(
            postsDirectory.resolve("2026-01-01-smoke-test.md"),
            "---\n" +
                "title: Smoke Test Post\n" +
                "author: smoke\n" +
                "---\n\n" +
                "Smoke test post content.\n",
            StandardCharsets.UTF_8
        );
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
