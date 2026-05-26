package com.robintegg.web.engine;

import com.robintegg.web.engine.ContentModel;
import com.robintegg.web.engine.ContentRenderer;
import com.robintegg.web.engine.ContentSource;
import com.robintegg.web.engine.Layout;
import com.robintegg.web.plugins.Plugins;
import com.robintegg.web.plugins.ThemePlugin;
import com.robintegg.web.site.Site;
import com.robintegg.web.site.SitePlugin;
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

        // register plugins
        themePlugin.registerPlugins();

        // load site configuration
        Site site = SitePlugin.loadFromFile(workingDirectory);

        if( baseUrl != null ) {
            site.setBaseUrl(baseUrl);
        }

        // define the source of content
        var contentSource = new ContentSource(site, workingDirectory);

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
        var outputDirectory = workingDirectory.resolve("target/site");
        log.info("output directory: {}", outputDirectory.toAbsolutePath());

        cleanOutputDirectory(outputDirectory);
        Files.createDirectories(outputDirectory);

        // create render engine

        ContentRenderer contentRenderer = new ContentRenderer();
        contentRenderer.render(outputDirectory, layouts, contentModel, context);

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
