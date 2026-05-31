package dev.rebelcraft.electrostatic.cli;

import com.robintegg.web.theme.DefaultThemePlugin;
import dev.rebelcraft.electrostatic.core.SiteGenerator;
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

    @Option(names = {"--base-url"}, description = "Override the base URL for the site")
    private String baseUrl;

    @Option(names = {"--input"}, description = "Input directory containing site content (default: src/main/resources/site)")
    private Path inputDirectory;

    @Option(names = {"--output"}, description = "Output directory for the generated site (default: target/site)")
    private Path outputDirectory;

    @Override
    public Integer call() throws Exception {
        var workingDir = Paths.get(System.getProperty("workingDirectory", ""));
        Path input = inputDirectory != null ? inputDirectory : workingDir.resolve("src/main/resources/site");
        Path output = outputDirectory != null ? outputDirectory : workingDir.resolve("target/site");
        new SiteGenerator(DefaultThemePlugin.create()).generate(input, output, baseUrl);
        return 0;
    }

}
