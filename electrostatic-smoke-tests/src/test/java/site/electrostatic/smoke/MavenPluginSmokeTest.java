package site.electrostatic.smoke;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import site.electrostatic.maven.GenerateMojo;
import site.electrostatic.maven.InitMojo;
import site.electrostatic.maven.ThemesMojo;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenPluginSmokeTest {

    @TempDir
    Path tempDir;

    @Test
    void themesGoalShouldListKnownThemeIds() throws Exception {
        ThemesMojo themesMojo = new ThemesMojo();
        CapturingLog capturingLog = new CapturingLog();
        themesMojo.setLog(capturingLog);

        themesMojo.execute();

        assertEquals(List.of(
            "Available Electrostatic themes:",
            "- default",
            "- docs",
            "- v2"
        ), capturingLog.infoMessages);
    }

    @Test
    void initAndGenerateGoalsShouldCreateExpectedSiteStructure() throws Exception {
        Path siteRoot = tempDir.resolve("plugin-site");
        Path outputDir = siteRoot.resolve("target/generated-site");

        InitMojo initMojo = new InitMojo();
        setField(initMojo, "rootDirectory", siteRoot.toFile());
        initMojo.execute();

        assertTrue(Files.exists(siteRoot.resolve("site-config.xml")));
        assertTrue(Files.exists(siteRoot.resolve("_posts/2026-01-01-hello-world.md")));

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
    void generateGoalShouldUseSiteConfigBaseUrlUnlessExplicitlyOverridden() throws Exception {
        Path siteRoot = tempDir.resolve("plugin-site-with-drafts");
        Path defaultOutputDir = siteRoot.resolve("target/generated-site-default");
        Path draftOutputDir = siteRoot.resolve("target/generated-site-drafts");

        InitMojo initMojo = new InitMojo();
        setField(initMojo, "rootDirectory", siteRoot.toFile());
        initMojo.execute();

        rewriteBaseUrl(siteRoot, "https://prod.example");
        rewriteDefaultPostToDatedPost(siteRoot);
        Files.writeString(
            siteRoot.resolve("_drafts/2026-01-02-draft-post.md"),
            "---\n" +
                "title: Draft Post\n" +
                "author: smoke\n" +
                "---\n\n" +
                "Draft body for Maven smoke test.\n",
            StandardCharsets.UTF_8
        );

        GenerateMojo defaultGenerateMojo = new GenerateMojo();
        setField(defaultGenerateMojo, "inputDirectory", siteRoot.toFile());
        setField(defaultGenerateMojo, "outputDirectory", defaultOutputDir.toFile());
        setField(defaultGenerateMojo, "compileClasspathElements", List.<String>of());
        defaultGenerateMojo.execute();

        assertTrue(Files.readString(defaultOutputDir.resolve("feed.xml")).contains("https://prod.example"));
        assertTrue(treeDoesNotContain(defaultOutputDir, "Draft body for Maven smoke test."));

        GenerateMojo draftGenerateMojo = new GenerateMojo();
        setField(draftGenerateMojo, "inputDirectory", siteRoot.toFile());
        setField(draftGenerateMojo, "outputDirectory", draftOutputDir.toFile());
        setField(draftGenerateMojo, "compileClasspathElements", List.<String>of());
        setField(draftGenerateMojo, "includeDrafts", true);
        setField(draftGenerateMojo, "baseUrl", "https://preview.example");
        draftGenerateMojo.execute();

        assertTrue(Files.readString(draftOutputDir.resolve("feed.xml")).contains("https://preview.example"));
        assertTrue(treeContains(draftOutputDir, "Draft body for Maven smoke test."));
    }

    @Test
    void generateGoalShouldAllowExplicitLocalhostShortcut() throws Exception {
        Path siteRoot = tempDir.resolve("plugin-site-localhost");
        Path outputDir = siteRoot.resolve("target/generated-site-localhost");

        InitMojo initMojo = new InitMojo();
        setField(initMojo, "rootDirectory", siteRoot.toFile());
        initMojo.execute();

        rewriteBaseUrl(siteRoot, "https://prod.example");
        rewriteDefaultPostToDatedPost(siteRoot);

        GenerateMojo generateMojo = new GenerateMojo();
        setField(generateMojo, "inputDirectory", siteRoot.toFile());
        setField(generateMojo, "outputDirectory", outputDir.toFile());
        setField(generateMojo, "compileClasspathElements", List.<String>of());
        setField(generateMojo, "baseUrl", "http://localhost:8080");
        generateMojo.execute();

        assertTrue(Files.readString(outputDir.resolve("feed.xml")).contains("http://localhost:8080"));
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
        assertTrue(Files.exists(siteDirectory.resolve("_posts/2026-01-01-hello-world.md")));
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

    @Test
    void initAndGenerateWithDocsThemeShouldProduceDocsSectionRoutes() throws Exception {
        Path siteRoot = tempDir.resolve("plugin-docs-site");
        Path outputDir = siteRoot.resolve("target/generated-site");

        InitMojo initMojo = new InitMojo();
        setField(initMojo, "rootDirectory", siteRoot.toFile());
        setField(initMojo, "theme", "docs");
        initMojo.execute();

        assertTrue(Files.exists(siteRoot.resolve("_index.md")));
        assertTrue(Files.exists(siteRoot.resolve("_installation/getting-started.md")));
        assertTrue(Files.exists(siteRoot.resolve("_guides/first-guide.md")));
        assertTrue(Files.exists(siteRoot.resolve("_plugins/plugin-overview.md")));

        GenerateMojo generateMojo = new GenerateMojo();
        setField(generateMojo, "inputDirectory", siteRoot.toFile());
        setField(generateMojo, "outputDirectory", outputDir.toFile());
        setField(generateMojo, "compileClasspathElements", List.<String>of());
        generateMojo.execute();

        assertTrue(Files.exists(outputDir.resolve("index.html")));
        assertTrue(Files.exists(outputDir.resolve("installation/index.html")));
        assertTrue(Files.exists(outputDir.resolve("guides/index.html")));
        assertTrue(Files.exists(outputDir.resolve("plugins/index.html")));
        assertTrue(Files.exists(outputDir.resolve("installation/getting-started.html")));
        assertTrue(Files.exists(outputDir.resolve("guides/first-guide.html")));
        assertTrue(Files.exists(outputDir.resolve("plugins/plugin-overview.html")));
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
        Files.deleteIfExists(postsDirectory.resolve("2026-01-01-hello-world.md"));
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

    private static void rewriteBaseUrl(Path siteRoot, String baseUrl) throws Exception {
        Path siteConfig = siteRoot.resolve("site-config.xml");
        String updated = Files.readString(siteConfig, StandardCharsets.UTF_8)
            .replace("<baseUrl>http://localhost:8080</baseUrl>", "<baseUrl>" + baseUrl + "</baseUrl>");
        Files.writeString(siteConfig, updated, StandardCharsets.UTF_8);
    }

    private static boolean treeContains(Path root, String expectedText) throws Exception {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                .filter(Files::isRegularFile)
                .anyMatch(path -> containsText(path, expectedText));
        }
    }

    private static boolean treeDoesNotContain(Path root, String expectedText) throws Exception {
        return !treeContains(root, expectedText);
    }

    private static boolean containsText(Path path, String expectedText) {
        try {
            return Files.readString(path).contains(expectedText);
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static final class CapturingLog implements Log {
        private final List<String> infoMessages = new ArrayList<>();

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(CharSequence content) {
            // Not needed in this test.
        }

        @Override
        public void debug(CharSequence content, Throwable error) {
            // Not needed in this test.
        }

        @Override
        public void debug(Throwable error) {
            // Not needed in this test.
        }

        @Override
        public boolean isInfoEnabled() {
            return true;
        }

        @Override
        public void info(CharSequence content) {
            infoMessages.add(content.toString());
        }

        @Override
        public void info(CharSequence content, Throwable error) {
            info(content);
        }

        @Override
        public void info(Throwable error) {
            // Not needed in this test.
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(CharSequence content) {
            // Not needed in this test.
        }

        @Override
        public void warn(CharSequence content, Throwable error) {
            // Not needed in this test.
        }

        @Override
        public void warn(Throwable error) {
            // Not needed in this test.
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void error(CharSequence content) {
            // Not needed in this test.
        }

        @Override
        public void error(CharSequence content, Throwable error) {
            // Not needed in this test.
        }

        @Override
        public void error(Throwable error) {
            // Not needed in this test.
        }
    }
}
