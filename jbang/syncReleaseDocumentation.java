///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class syncReleaseDocumentation {

    private static final Pattern RELEASE_VERSION = Pattern.compile("\\d+(?:\\.\\d+)+");
    private static final Pattern CLI_COORDINATE = Pattern.compile(
            "(io\\.github\\.teggr:electrostatic-cli:)(\\d+(?:\\.\\d+)+(?:-[A-Za-z0-9.-]+)?)(?=[:\\s`])");
    private static final Pattern PLUGIN_COORDINATE = Pattern.compile(
            "(io\\.github\\.teggr:electrostatic-maven-plugin:)(\\d+(?:\\.\\d+)+(?:-[A-Za-z0-9.-]+)?)(?=[:\\s`])");
    private static final Pattern PLUGIN_XML_VERSION = Pattern.compile(
            "(<artifactId>electrostatic-maven-plugin</artifactId>\\s*<version>)[^<]+(</version>)");
    private static final Pattern DEVELOPMENT_VERSION = Pattern.compile(
            "(?m)^(The current development version of Electrostatic is `)[^`\\r\\n]+(`, as declared by the root POM\\.)");

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && "--self-test".equals(args[0])) {
            selfTest();
            return;
        }
        if (args.length != 1 || args[0].isBlank()) {
            throw new IllegalArgumentException(
                    "Usage: jbang syncReleaseDocumentation.java <release-version> | --self-test");
        }
        String releaseVersion = args[0];
        if (!RELEASE_VERSION.matcher(releaseVersion).matches()) {
            throw new IllegalArgumentException("Expected a stable release version, got: " + releaseVersion);
        }

        Path root = findRepositoryRoot();
        String snapshotVersion = readRootVersion(root.resolve("pom.xml"));
        if (!snapshotVersion.endsWith("-SNAPSHOT")) {
            throw new IllegalStateException(
                    "The root POM must contain the next -SNAPSHOT version before syncing documentation.");
        }

        Path readmePath = root.resolve("README.md");
        Path releasePath = root.resolve("docs/RELEASE.md");
        String readme = Files.readString(readmePath);
        String releaseDoc = Files.readString(releasePath);
        String updatedReadme = replaceAll(readme, CLI_COORDINATE, releaseVersion, 1, "README CLI coordinates");
        updatedReadme = replaceAll(updatedReadme, PLUGIN_COORDINATE, releaseVersion, 1,
                "README Maven plugin coordinates");
        updatedReadme = replaceAllXmlVersions(updatedReadme, releaseVersion, 1,
                "README Maven plugin declaration");
        updatedReadme = replaceExactlyOnce(updatedReadme, DEVELOPMENT_VERSION, snapshotVersion,
                "README development version");
        String updatedReleaseDoc = replaceExactlyOnce(releaseDoc, DEVELOPMENT_VERSION, snapshotVersion,
                "release guide development version");

        Path siteDocsPath = root.resolve("electrostatic-run/src/main/resources/site");
        List<Path> siteDocs;
        try (var paths = Files.walk(siteDocsPath)) {
            siteDocs = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".md"))
                    .sorted()
                    .toList();
        }

        Map<Path, String> updates = new LinkedHashMap<>();
        updates.put(readmePath, updatedReadme);
        updates.put(releasePath, updatedReleaseDoc);
        int cliReferences = 0;
        int pluginXmlReferences = 0;
        for (Path siteDoc : siteDocs) {
            String content = Files.readString(siteDoc);
            cliReferences += countMatches(content, CLI_COORDINATE);
            pluginXmlReferences += countMatches(content, PLUGIN_XML_VERSION);

            String updated = replaceAll(content, CLI_COORDINATE, releaseVersion, 0, siteDoc.toString() + " CLI coordinates");
            updated = replaceAll(updated, PLUGIN_COORDINATE, releaseVersion, 0,
                    siteDoc.toString() + " Maven plugin coordinates");
            updated = replaceAllXmlVersions(updated, releaseVersion, 0, siteDoc.toString() + " Maven plugin declaration");
            updates.put(siteDoc, updated);
        }
        if (cliReferences == 0 || pluginXmlReferences == 0) {
            throw new IllegalStateException("Could not find CLI and Maven plugin version references in documentation-site Markdown.");
        }

        for (Map.Entry<Path, String> update : updates.entrySet()) {
            Files.writeString(update.getKey(), update.getValue());
        }
        System.out.println("Synchronized README.md, docs/RELEASE.md, and documentation-site Markdown for "
                + releaseVersion + ".");
    }

    private static Path findRepositoryRoot() {
        Path path = Path.of("").toAbsolutePath().normalize();
        while (path != null) {
            if (Files.isRegularFile(path.resolve("pom.xml")) && Files.isRegularFile(path.resolve("README.md"))) {
                return path;
            }
            path = path.getParent();
        }
        throw new IllegalStateException("Run this script from within the Electrostatic repository.");
    }

    private static String readRootVersion(Path pomPath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        Element project = factory.newDocumentBuilder().parse(pomPath.toFile()).getDocumentElement();
        for (Node child = project.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element && "version".equals(element.getLocalName())) {
                return element.getTextContent().trim();
            }
        }
        throw new IllegalStateException("The root POM has no project version.");
    }

    private static String replaceAll(
            String content, Pattern pattern, String version, int minimumCount, String label) {
        Matcher matcher = pattern.matcher(content);
        StringBuffer updated = new StringBuffer();
        int count = 0;
        while (matcher.find()) {
            matcher.appendReplacement(updated, Matcher.quoteReplacement(matcher.group(1) + version));
            count++;
        }
        matcher.appendTail(updated);
        if (count < minimumCount) {
            throw new IllegalStateException("Could not find version references for " + label);
        }
        return updated.toString();
    }

    private static String replaceAllXmlVersions(String content, String version, int minimumCount, String label) {
        Matcher matcher = PLUGIN_XML_VERSION.matcher(content);
        StringBuffer updated = new StringBuffer();
        int count = 0;
        while (matcher.find()) {
            matcher.appendReplacement(updated,
                    Matcher.quoteReplacement(matcher.group(1) + version + matcher.group(2)));
            count++;
        }
        matcher.appendTail(updated);
        if (count < minimumCount) {
            throw new IllegalStateException("Could not find version references for " + label);
        }
        return updated.toString();
    }

    private static int countMatches(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static String replaceExactlyOnce(String content, Pattern pattern, String version, String label) {
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not find the version reference for " + label);
        }
        String replacement = matcher.group(1) + version + matcher.group(2);
        int start = matcher.start();
        int end = matcher.end();
        if (matcher.find()) {
            throw new IllegalStateException("Found multiple version references for " + label);
        }
        return content.substring(0, start) + replacement + content.substring(end);
    }

    private static void selfTest() {
        String readme = """
                jbang io.github.teggr:electrostatic-cli:0.0.1-SNAPSHOT build
                mvn io.github.teggr:electrostatic-maven-plugin:0.0.1-SNAPSHOT:init
                <artifactId>electrostatic-maven-plugin</artifactId><version>0.0.1-SNAPSHOT</version>
                <artifactId>electrostatic-maven-plugin</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                """;
        String updatedReadme = replaceAll(readme, CLI_COORDINATE, "1.2.3", 1, "test CLI coordinate");
        updatedReadme = replaceAll(updatedReadme, PLUGIN_COORDINATE, "1.2.3", 1, "test plugin coordinate");
        updatedReadme = replaceAllXmlVersions(updatedReadme, "1.2.3", 1, "test plugin declaration");
        requireContains(updatedReadme, "io.github.teggr:electrostatic-cli:1.2.3 build");
        requireContains(updatedReadme, "io.github.teggr:electrostatic-maven-plugin:1.2.3:init");
        requireContains(updatedReadme, "<version>1.2.3</version>");
        if (countMatches(updatedReadme, PLUGIN_XML_VERSION) != 2 || updatedReadme.contains("0.0.1-SNAPSHOT")) {
            throw new IllegalStateException("The self-test did not update every README Maven plugin declaration.");
        }
        String updatedReadmeDevelopmentVersion = replaceExactlyOnce(
                "The current development version of Electrostatic is `stale`, as declared by the root POM.",
                DEVELOPMENT_VERSION, "1.2.4-SNAPSHOT", "test README development version");
        requireContains(updatedReadmeDevelopmentVersion, "`1.2.4-SNAPSHOT`");

        String siteDoc = """
                jbang io.github.teggr:electrostatic-cli:0.0.1-SNAPSHOT build
                <artifactId>electrostatic-maven-plugin</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                """;
        String updatedSiteDoc = replaceAll(siteDoc, CLI_COORDINATE, "1.2.3", 1, "test site CLI coordinate");
        updatedSiteDoc = replaceAllXmlVersions(updatedSiteDoc, "1.2.3", 1, "test site Maven plugin declaration");
        requireContains(updatedSiteDoc, "io.github.teggr:electrostatic-cli:1.2.3 build");
        requireContains(updatedSiteDoc, "<version>1.2.3</version>");
        if (updatedSiteDoc.contains("0.0.1-SNAPSHOT")) {
            throw new IllegalStateException("The self-test did not update documentation-site versions.");
        }

        String releaseDoc =
                "The current development version of Electrostatic is `stale`, as declared by the root POM.";
        requireContains(replaceExactlyOnce(releaseDoc, DEVELOPMENT_VERSION, "1.2.4-SNAPSHOT",
                "test development docs"), "`1.2.4-SNAPSHOT`");

        try {
            replaceExactlyOnce(releaseDoc + "\n" + releaseDoc, DEVELOPMENT_VERSION, "1.2.4-SNAPSHOT",
                    "duplicate test");
            throw new IllegalStateException("The self-test did not reject duplicate version references.");
        } catch (IllegalStateException expected) {
            if (!expected.getMessage().contains("multiple")) {
                throw expected;
            }
        }
        System.out.println("Documentation synchronization self-test passed.");
    }

    private static void requireContains(String content, String expected) {
        if (!content.contains(expected)) {
            throw new IllegalStateException("Documentation synchronization self-test failed: " + expected);
        }
    }
}
