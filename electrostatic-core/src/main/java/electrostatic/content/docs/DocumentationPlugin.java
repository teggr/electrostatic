package electrostatic.content.docs;

import electrostatic.content.podcast.Podcast;
import electrostatic.content.podcast.PodcastPlugin;
import electrostatic.engine.ContentModel;
import electrostatic.pages.PagesPlugin;
import electrostatic.plugins.ContentTypePlugin;
import electrostatic.plugins.Plugins;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class DocumentationPlugin implements ContentTypePlugin {
    public static DocumentationPlugin create() {
        return new DocumentationPlugin();
    }

    @SneakyThrows
    @Override
    public void loadContent(Path sourceDirectory, ContentModel contentModel) {
        // load documentation from folder with markdown
        var docsDirectory = sourceDirectory.resolve("_docs");
        log.info("docs directory: " + docsDirectory.toAbsolutePath());

        if(Files.exists(docsDirectory)) {
            try (Stream<Path> paths = Files.walk(docsDirectory)) {
                paths
                        .filter(Files::isRegularFile)
                        .peek(f -> log.info("{}", f))
                        .map(DocumentationPlugin::readDocumentation)
                        .forEach(contentModel::add);
            }
        }

    }

    private static Documentation readDocumentation(Path path) {

        try {
            // Extract filename, filename without extension, and extension using Path methods
            String filename = path.getFileName().toString();
            int dotIndex = filename.lastIndexOf('.');
            String filenameWithoutExtension = (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
            String fileExtension = (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);

            if (fileExtension.equals("md")) {

                Extension extension = YamlFrontMatterExtension.create();
                Parser parser = Parser.builder()
                        .extensions(List.of(extension))
                        .build();

                Node document = parser.parseReader(Files.newBufferedReader(path));

                YamlFrontMatterVisitor yamlFrontMatterVisitor = new YamlFrontMatterVisitor();
                document.accept(yamlFrontMatterVisitor);

                return new Documentation(filenameWithoutExtension, yamlFrontMatterVisitor.getData(), document);

            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void register() {
        Plugins.contentTypePlugins.add(this);
    }

}
