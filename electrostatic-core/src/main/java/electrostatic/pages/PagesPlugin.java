package electrostatic.pages;

import electrostatic.content.podcast.Podcast;
import electrostatic.content.podcast.PodcastPlugin;
import electrostatic.engine.ContentModel;
import electrostatic.engine.Page;
import electrostatic.plugins.ContentTypePlugin;
import electrostatic.plugins.Plugins;
import electrostatic.utils.IndentedCodeBlockNodeRenderer;
import j2html.TagCreator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class PagesPlugin implements ContentTypePlugin {

    public static PagesPlugin create() {
        return new PagesPlugin();
    }

    public final List<Page> pages = new ArrayList<>();


    public void addPage(Page page) {
        pages.add(page);
    }

    @SneakyThrows
    @Override
    public void loadContent(Path sourceDirectory, ContentModel contentModel) {

        // load pages from folder with markdown
        var pagesDirectory = sourceDirectory.resolve("_pages");
        log.info("pages directory: " + pagesDirectory.toAbsolutePath());

        if(Files.exists(pagesDirectory)) {
            try (Stream<Path> paths = Files.walk(pagesDirectory)) {
                paths
                        .filter(Files::isRegularFile)
                        .peek(f -> log.info("{}", f))
                        .map(PagesPlugin::readPage)
                        .forEach(contentModel::addPage);
            }
        }

        pages.forEach(contentModel::addPage);

    }

    private static Page readPage(Path path) {

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

                return Page.builder()
                        .path(filenameWithoutExtension + ".html")
                        .data(yamlFrontMatterVisitor.getData())
                        .renderFunction((renderModel -> {
                            document.accept(new AbstractVisitor() {
                                @Override
                                public void visit(Image image) {
                                    image.setDestination(image.getDestination().replaceAll("\\{\\{site\\.baseurl\\}\\}", renderModel.getContext().getSite().getBaseUrl()));
                                    super.visit(image);
                                }
                            });
                            HtmlRenderer renderer = HtmlRenderer.builder()
                                    .nodeRendererFactory(new HtmlNodeRendererFactory() {
                                        public NodeRenderer create(HtmlNodeRendererContext context) {
                                            return new IndentedCodeBlockNodeRenderer(context);
                                        }
                                    })
                                    .build();
                            return TagCreator.rawHtml(
                                    renderer.render(document)
                            );
                        }))
                        .build();

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
