package site.electrostatic.engine;

import site.electrostatic.core.GenerationOptions;
import site.electrostatic.core.GenerationOptionsContext;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.plugins.ThemePlugin;
import site.electrostatic.site.Site;
import site.electrostatic.site.SitePlugin;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WebSiteBuilder {

    private final ThemePlugin themePlugin;

    @SneakyThrows
    public void build( String baseUrl ) {

        // TODO: whilst multi-module in Intellij - must set workdirectory to the module root, unless we set it to absolute?
        var workingDirectory = Paths.get(System.getProperty("workingDirectory", ""));
        log.info("working directory: {}", workingDirectory.toAbsolutePath());

        build(GenerationOptions.fromBaseUrl(baseUrl), workingDirectory, workingDirectory.resolve("target/site"));

    }

    @SneakyThrows
    public void build( String baseUrl, java.nio.file.Path inputDirectory, java.nio.file.Path outputDirectory ) {
        build(GenerationOptions.fromBaseUrl(baseUrl), inputDirectory, outputDirectory);
    }

    @SneakyThrows
    public void build(GenerationOptions options) {

        var workingDirectory = Paths.get(System.getProperty("workingDirectory", ""));
        log.info("working directory: {}", workingDirectory.toAbsolutePath());

        build(options, workingDirectory, workingDirectory.resolve("target/site"));
    }

    @SneakyThrows
    public void build(GenerationOptions options, java.nio.file.Path inputDirectory, java.nio.file.Path outputDirectory ) {

        log.info("input directory: {}", inputDirectory.toAbsolutePath());
        log.info("output directory: {}", outputDirectory.toAbsolutePath());

        GenerationOptionsContext.set(options);
        try {
            // register plugins
            themePlugin.registerPlugins();

            // load site configuration
            Site site = SitePlugin.loadFromFile(inputDirectory);

            if( options.baseUrl() != null ) {
                site.setBaseUrl(options.baseUrl());
            }
            GenerationOptionsContext.set(options.withBaseUrl(site.getBaseUrl()));

            // define the source of content
            var contentSource = new ContentSource(site, inputDirectory);

            // load content into model and populate plugins
            ContentModel contentModel = new ContentModel();
            contentSource.loadContent(contentModel);

            // load rendering engine context
            Context context = new Context();

            String environment = System.getProperty("environment", "local");
            log.info("environment: {}", environment);

            context.setEnvironment(environment);
            context.setSite(site);

            // load layouts
            Map<String, Layout> layouts = new HashMap<>();
            Plugins.contentRenderPlugins.stream()
                    .forEach(contentRenderPlugin -> contentRenderPlugin.loadLayout(layouts));

            // TODO: filesystem plugin for output
            // create output directory
            cleanOutputDirectory(outputDirectory);
            Files.createDirectories(outputDirectory);

            // create render engine
            ContentRenderer contentRenderer = new ContentRenderer();
            contentRenderer.render(outputDirectory, layouts, contentModel, context);
        } finally {
            GenerationOptionsContext.clear();
        }

    }

    @SneakyThrows
    static void cleanOutputDirectory(java.nio.file.Path outputDirectory) {
        if (!Files.exists(outputDirectory)) {
            return;
        }

        try (var paths = Files.walk(outputDirectory)) {
            paths.sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        }
    }

}
