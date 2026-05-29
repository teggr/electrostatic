package dev.rebelcraft.electrostatic.cli;

import com.robintegg.web.engine.WebSiteBuilder;
import com.robintegg.web.theme.DefaultThemePlugin;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
    name = "build",
    mixinStandardHelpOptions = true,
    description = "Build the static site"
)
public class BuildCommand implements Callable<Integer> {

    @Option(names = {"--base-url"}, description = "Override the base URL for the site")
    private String baseUrl;

    @Override
    public Integer call() throws Exception {
        new WebSiteBuilder(DefaultThemePlugin.create()).build(baseUrl);
        return 0;
    }

}
