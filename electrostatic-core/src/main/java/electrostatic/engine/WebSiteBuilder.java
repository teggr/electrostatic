package electrostatic.engine;

import electrostatic.plugins.Plugins;
import electrostatic.plugins.ThemePlugin;
import electrostatic.site.Site;
import electrostatic.site.SitePlugin;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WebSiteBuilder {

    private final ThemePlugin themePlugin;

    @SneakyThrows
    public void build( String workingDirectoryPath, String environment ) {

        // TODO: whilst multi-module in Intellij - must set workdirectory to the module root, unless we set it to absolute?
        var workingDirectory = Paths.get(workingDirectoryPath);
        log.info("working directory: {}", workingDirectory.toAbsolutePath());

        // register plugins
        themePlugin.registerPlugins();

        // define the source of content
        var contentSource = new ContentSource(workingDirectory);

        // load content into model and populate plugins
        ContentModel contentModel = new ContentModel();
        contentSource.loadContent(contentModel);

        // load rendering engine context
        Context context = new Context();

        log.info("environment: {}", environment);

        context.setEnvironment(environment);

        Site site = SitePlugin.loadFromFile(workingDirectory);

        context.setSite(site);

        // load layouts
        Map<String, Layout> layouts = new HashMap<>();
        Plugins.contentRenderPlugins.stream()
                .forEach(contentRenderPlugin -> contentRenderPlugin.loadLayout(layouts));

        // TODO: filesystem plugin for output
        // create output directory
        var outputDirectory = workingDirectory.resolve("target/site");
        log.info("output directory: {}", outputDirectory.toAbsolutePath());

        Files.createDirectories(outputDirectory);

        // create render engine

        ContentRenderer contentRenderer = new ContentRenderer();
        contentRenderer.render(outputDirectory, layouts, contentModel, context);

    }

}
