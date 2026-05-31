package run.electrostatic.cli;

import picocli.CommandLine.Command;
import run.electrostatic.theme.ThemePlugins;

import java.util.concurrent.Callable;

@Command(
    name = "themes",
    mixinStandardHelpOptions = true,
    description = "List available theme bundles"
)
public class ThemesCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        ThemePlugins.availableThemeIds().forEach(System.out::println);
        return 0;
    }
}