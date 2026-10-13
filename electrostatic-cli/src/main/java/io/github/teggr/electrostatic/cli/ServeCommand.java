package io.github.teggr.electrostatic.cli;

import io.github.teggr.electrostatic.core.GenerationOptions;
import io.github.teggr.electrostatic.core.SitePreviewServer;
import io.github.teggr.electrostatic.theme.ThemePlugins;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(
    name = "serve",
    mixinStandardHelpOptions = true,
    description = "Build the static site and serve it locally"
)
public class ServeCommand implements Callable<Integer> {

    @Option(names = {"-p", "--port"}, defaultValue = "8080", description = "Port to serve on (default: ${DEFAULT-VALUE})")
    private int port;

    @Option(
        names = {"--base-url"},
        description = "Base URL for the site (defaults to site-config.xml; pass http://localhost:8080 for a local preview shortcut)"
    )
    private String baseUrl;

    @Option(names = {"--include-drafts"}, defaultValue = "false", description = "Include content from _drafts (default: ${DEFAULT-VALUE})")
    private boolean includeDrafts;

    @Option(names = {"--watch"}, description = "Rebuild on source changes and automatically reload the browser")
    private boolean watch;

    @Option(names = {"--input"}, description = "Input directory containing site content (default: current working directory)")
    private Path inputDirectory;

    @Option(names = {"--output"}, description = "Output directory for the generated site (default: ./generated-site)")
    private Path outputDirectory;

    @Option(names = {"--theme"}, description = "Theme bundle to use (default, docs, v2). Defaults to <theme> in site-config.xml, then default.")
    private String theme;

    @Override
    public Integer call() throws Exception {
        var workingDir = Paths.get(System.getProperty("workingDirectory", ""));
        Path projectRoot = workingDir.toAbsolutePath().normalize();
        Path input = inputDirectory != null ? inputDirectory : projectRoot;
        Path siteDirectory = (outputDirectory != null ? outputDirectory : projectRoot.resolve("generated-site"))
            .toAbsolutePath()
            .normalize();
        GenerationOptions options = GenerationOptions.defaults()
            .withBaseUrl(baseUrl)
            .withIncludeDrafts(includeDrafts);

        SitePreviewServer.PreviewSession session = new SitePreviewServer(ThemePlugins.resolveForSite(theme, input))
            .start(input, siteDirectory, options, port, projectRoot, watch);
        Runtime.getRuntime().addShutdownHook(new Thread(session::close));

        System.out.println("Serving site from " + siteDirectory);
        System.out.println("Listening on http://localhost:" + port);
        if (watch) {
            System.out.println("Watching for changes in " + input.toAbsolutePath().normalize());
        }
        System.out.println("Press Ctrl+C to stop.");

        try {
            Thread.currentThread().join();
        } finally {
            session.close();
        }

        return 0;
    }

}
