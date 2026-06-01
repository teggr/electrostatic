package run.electrostatic.cli;

import run.electrostatic.core.GenerationOptions;
import run.electrostatic.core.SiteGenerator;
import run.electrostatic.theme.ThemePlugins;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(
    name = "build",
    mixinStandardHelpOptions = true,
    description = "Build the static site"
)
public class BuildCommand implements Callable<Integer> {

    @Option(
        names = {"--base-url"},
        description = "Base URL for the site (defaults to site-config.xml; pass http://localhost:8080 for a local preview shortcut)"
    )
    private String baseUrl;

    @Option(names = {"--include-drafts"}, defaultValue = "false", description = "Include content from _drafts (default: ${DEFAULT-VALUE})")
    private boolean includeDrafts;

    @Option(names = {"--input"}, description = "Input directory containing site content (default: current working directory)")
    private Path inputDirectory;

    @Option(names = {"--output"}, description = "Output directory for the generated site (default: ./generated-site)")
    private Path outputDirectory;

    @Option(names = {"--theme"}, description = "Theme bundle to use (default, docs, v2). Defaults to <theme> in site-config.xml, then default.")
    private String theme;

    @Override
    public Integer call() throws Exception {
        var workingDir = Paths.get(System.getProperty("workingDirectory", ""));
        Path input = inputDirectory != null ? inputDirectory : workingDir;
        Path output = outputDirectory != null ? outputDirectory : workingDir.resolve("generated-site");
        GenerationOptions options = GenerationOptions.defaults()
            .withBaseUrl(baseUrl)
            .withIncludeDrafts(includeDrafts);
        new SiteGenerator(ThemePlugins.resolveForSite(theme, input)).generate(input, output, options);
        return 0;
    }

}
