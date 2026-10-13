///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class checkProjectConsistency {

    private static final Pattern ARTIFACT_PATTERN =
            Pattern.compile("\\belectrostatic-[a-z0-9-]+\\b");
    private static final Pattern COORDINATE_PATTERN = Pattern.compile(
            "<groupId>\\s*([^<]+)\\s*</groupId>\\s*<artifactId>\\s*([^<]+)\\s*</artifactId>"
                    + "|([\\w.-]+)\\s*:\\s*(electrostatic-[\\w.-]+)");
    private static final Pattern MARKDOWN_LINK_PATTERN =
            Pattern.compile("!?\\[[^\\]]*\\]\\(([^)]+)\\)");

    public static void main(String[] args) throws Exception {
        Path root = findRepositoryRoot();
        String groupId = childText(parseXml(root.resolve("pom.xml")).getDocumentElement(), "groupId");
        Set<String> artifacts = inventoryArtifacts(root, groupId);
        List<String> errors = new ArrayList<>();
        List<Path> markdownFiles = markdownFiles(root);
        for (Path markdown : markdownFiles) {
            checkArtifactNames(root, markdown, artifacts, errors);
            checkLinks(root, markdown, errors);
        }

        if (!errors.isEmpty()) {
            errors.forEach(error -> System.err.println("ERROR: " + error));
            System.exit(1);
        }
        System.out.printf("Project consistency check passed: %d project artifacts, %d Markdown files.%n",
                artifacts.size(), markdownFiles.size());
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

    private static Set<String> inventoryArtifacts(Path root, String groupId) throws Exception {
        Set<String> artifacts = new HashSet<>();
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path pom : paths
                    .filter(path -> path.getFileName().toString().equals("pom.xml"))
                    .filter(path -> !path.toString().contains(".git"))
                    .filter(path -> !path.toString().contains("target"))
                    .toList()) {
                Element project = parseXml(pom).getDocumentElement();
                String projectGroup = childText(project, "groupId");
                if (projectGroup == null) {
                    Element parent = childElement(project, "parent");
                    projectGroup = parent == null ? null : childText(parent, "groupId");
                }
                if (groupId.equals(projectGroup)) {
                    String artifactId = childText(project, "artifactId");
                    if (artifactId != null) {
                        artifacts.add(artifactId);
                    }
                }
            }
        }
        return artifacts;
    }

    private static List<Path> markdownFiles(Path root) throws Exception {
        List<Path> markdownFiles = new ArrayList<>();
        markdownFiles.add(root.resolve("README.md"));
        Path docs = root.resolve("docs");
        if (Files.isDirectory(docs)) {
            try (Stream<Path> paths = Files.walk(docs)) {
                markdownFiles.addAll(paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".md"))
                        .sorted()
                        .toList());
            }
        }
        return markdownFiles;
    }

    private static void checkArtifactNames(
            Path root, Path markdown, Set<String> artifacts, List<String> errors) throws Exception {
        String content = Files.readString(markdown);
        String relativePath = root.relativize(markdown).toString().replace('\\', '/');
        Matcher artifactMatcher = ARTIFACT_PATTERN.matcher(content);
        while (artifactMatcher.find()) {
            String artifactId = artifactMatcher.group();
            if (!artifacts.contains(artifactId)) {
                errors.add(relativePath + " mentions unknown artifact " + artifactId);
            }
        }

        Matcher coordinateMatcher = COORDINATE_PATTERN.matcher(content);
        while (coordinateMatcher.find()) {
            String coordinateGroup = coordinateMatcher.group(1) != null
                    ? coordinateMatcher.group(1).trim()
                    : coordinateMatcher.group(3).trim();
            String artifactId = coordinateMatcher.group(2) != null
                    ? coordinateMatcher.group(2).trim()
                    : coordinateMatcher.group(4).trim();
            if (coordinateGroup.equals("io.github.teggr") && !artifacts.contains(artifactId)) {
                errors.add(relativePath + " uses unknown coordinate " + coordinateGroup + ":" + artifactId);
            }
        }
    }

    private static void checkLinks(Path root, Path markdown, List<String> errors) throws Exception {
        String content = Files.readString(markdown);
        String relativePath = root.relativize(markdown).toString().replace('\\', '/');
        Matcher matcher = MARKDOWN_LINK_PATTERN.matcher(content);
        while (matcher.find()) {
            String target = matcher.group(1).trim();
            int titleSeparator = target.indexOf(' ');
            if (titleSeparator >= 0) {
                target = target.substring(0, titleSeparator);
            }
            target = target.replace("<", "").replace(">", "");
            URI uri = URI.create(target.replace(" ", "%20"));
            if (uri.getScheme() != null || uri.getAuthority() != null || uri.getPath() == null
                    || uri.getPath().isEmpty()) {
                continue;
            }
            String linkPath = uri.getPath().replace("%20", " ");
            Path destination = linkPath.startsWith("/")
                    ? root.resolve(linkPath.substring(1))
                    : markdown.getParent().resolve(linkPath);
            if (!Files.exists(destination.normalize())) {
                errors.add(relativePath + " links to missing path " + uri.getPath());
            }
        }
    }

    private static Element childElement(Element parent, String name) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE && name.equals(child.getLocalName())) {
                return (Element) child;
            }
        }
        return null;
    }

    private static String childText(Element parent, String name) {
        Element child = childElement(parent, name);
        return child == null ? null : child.getTextContent().trim();
    }

    private static Document parseXml(Path path) throws Exception {
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
        return factory.newDocumentBuilder().parse(path.toFile());
    }
}
