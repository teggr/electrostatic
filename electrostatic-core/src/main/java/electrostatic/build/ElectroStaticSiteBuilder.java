package electrostatic.build;

import electrostatic.plugins.ClasspathPluginContext;
import electrostatic.plugins.ThemePlugin;
import electrostatic.theme.home.HomeThemePlugin;
import electrostatic.utils.ClasspathScanner;
import electrostatic.website.WebSiteBuilder;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Builder
@Slf4j
public class ElectroStaticSiteBuilder {

    private final BuildContext buildContext;

    @SneakyThrows
    public static void clean(String workingDirectory) {

        // create output directory
        var outputDirectory = Path.of(workingDirectory).resolve("target/site");
        log.info("output directory: {}", outputDirectory.toAbsolutePath());

        if (Files.exists(outputDirectory)) {
            Files.walk(outputDirectory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

    }

    @SneakyThrows
    public void build() {

        String[] scanPackages = new String[]{"electrostatic"};
        if (!buildContext.getBasePackage().isBlank()) {
            scanPackages = new String[]{buildContext.getBasePackage(), "electrostatic"};
        }

        try (ClasspathScanner classpathScanner =
                     new ClasspathScanner(scanPackages)) {

            log.info("loading theme");

            // load theme
            ThemePlugin themePlugin = findTheme(buildContext.getThemeName(), classpathScanner);

            // register the theme plugins
            themePlugin.registerPlugins(new ClasspathPluginContext(classpathScanner), buildContext);

            // build website
            WebSiteBuilder webSiteBuilder = new WebSiteBuilder(buildContext);
            webSiteBuilder.build();

        } catch (Exception e) {
            log.error("electrostatic failed to complete build", e);
            throw e;
        }

    }

    @SneakyThrows
    private static ThemePlugin findTheme(String themeName, ClasspathScanner scanner) {

        // look for any theme plugins on the classpath
        List<String> themePluginClassnames = scanner.findClassesImplementing(ThemePlugin.class);

        // choose a theme
        String chosenThemePluginClassname = "";

        if (themePluginClassnames.isEmpty()) {

            throw new RuntimeException("no themes found");

        }

        // if the build has defined a theme, that is first pick
        List<String> customPlugins = themePluginClassnames.stream()
                .filter(ElectroStaticSiteBuilder::isCustomPlugin)
                .toList();

        if (customPlugins.isEmpty()) {

            // pick a default plugin
            if (themeName.isBlank()) {

                // choose the first available theme plugin
                chosenThemePluginClassname = themePluginClassnames.get(0);

            } else {

                // find a matching named theme plugin
                // first classname containing the theme name
                chosenThemePluginClassname = themePluginClassnames.stream()
                        .filter(cn -> cn.toLowerCase().contains(themeName.toLowerCase()))
                        .findFirst()
                        .orElseThrow();

            }

        } else {

            // pick first available custom plugin
            chosenThemePluginClassname = customPlugins.get(0);

        }


        log.info("chosen theme plugin {}", chosenThemePluginClassname);

        return (ThemePlugin) Class.forName(chosenThemePluginClassname)
                .getDeclaredConstructor()
                .newInstance();

    }

    private static boolean isCustomPlugin(String pluginClassName) {
        return !Stream.of(HomeThemePlugin.class)
                .map(Class::getName)
                .toList()
                .contains(pluginClassName);
    }

}
