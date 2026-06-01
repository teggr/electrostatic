package site.electrostatic.cli;

import site.electrostatic.core.SiteInitializer;
import site.electrostatic.theme.ThemePlugins;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(
    name = "init",
    mixinStandardHelpOptions = true,
    description = "Initialize a new Electrostatic site"
)
public class InitCommand implements Callable<Integer> {

    @Parameters(
        index = "0",
        arity = "0..1",
        paramLabel = "ROOT_DIRECTORY",
        description = "Root directory to initialize (default: current directory)"
    )
    private Path rootDirectory;

    @Option(names = {"--theme"}, defaultValue = "default", description = "Theme bundle to use (default, docs, v2)")
    private String theme;

    @Override
    public Integer call() {
        Path workingDir = Paths.get(System.getProperty("workingDirectory", ""));
        Path root = rootDirectory != null ? rootDirectory : workingDir;

        try {
            new SiteInitializer(ThemePlugins.resolve(theme)).initialize(root);
            System.out.println("Initialized Electrostatic site at " + root.toAbsolutePath().normalize());
            return 0;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return 1;
        }
    }
}