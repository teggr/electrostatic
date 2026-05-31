package run.electrostatic.cli;

import run.electrostatic.plugins.Plugins;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InitCommandTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        System.clearProperty("workingDirectory");
    }

    @AfterEach
    void tearDown() {
        Plugins.initializationPlugins.clear();
        Plugins.contentTypePlugins.clear();
        Plugins.aggregatorPlugins.clear();
        Plugins.contentRenderPlugins.clear();
        System.clearProperty("workingDirectory");
    }

    @Test
    void execute_shouldInitializeWorkingDirectoryWhenRootNotProvided() {
        Path root = tempDir.resolve("site-from-working-dir");
        System.setProperty("workingDirectory", root.toString());

        int exitCode = new CommandLine(new InitCommand()).execute();

        assertEquals(0, exitCode);
        assertTrue(Files.exists(root.resolve("site-config.xml")));
        assertTrue(Files.exists(root.resolve("_posts/hello-world.md")));
    }

    @Test
    void execute_shouldInitializeProvidedRootDirectoryArgument() {
        Path root = tempDir.resolve("site-from-arg");

        int exitCode = new CommandLine(new InitCommand()).execute(root.toString());

        assertEquals(0, exitCode);
        assertTrue(Files.exists(root.resolve("site-config.xml")));
        assertTrue(Files.exists(root.resolve("_posts/hello-world.md")));
    }

    @Test
    void execute_shouldFailForNonEmptyDirectory() throws Exception {
        Path root = tempDir.resolve("non-empty-site");
        Files.createDirectories(root);
        Files.writeString(root.resolve("already.txt"), "existing");

        int exitCode = new CommandLine(new InitCommand()).execute(root.toString());

        assertEquals(1, exitCode);
    }
}
