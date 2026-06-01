package site.electrostatic.theme;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import site.electrostatic.plugins.ThemePlugin;
import site.electrostatic.theme.docs.DocsThemePlugin;
import site.electrostatic.theme.v2.V2ThemePlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThemePluginsTest {

    @TempDir
    Path tempDir;

    @Test
    void availableThemeIds_shouldReturnKnownThemeIdsInStableOrder() {
        assertEquals(List.of("default", "docs", "v2"), ThemePlugins.availableThemeIds());
    }

    @Test
    void resolve_shouldReturnKnownBundles() {
        ThemePlugin defaultTheme = ThemePlugins.resolve("default");
        ThemePlugin docsTheme = ThemePlugins.resolve("docs");
        ThemePlugin v2Theme = ThemePlugins.resolve("v2");

        assertInstanceOf(DefaultThemePlugin.class, defaultTheme);
        assertInstanceOf(DocsThemePlugin.class, docsTheme);
        assertInstanceOf(V2ThemePlugin.class, v2Theme);
    }

    @Test
    void resolve_shouldRejectUnknownTheme() {
        assertThrows(IllegalArgumentException.class, () -> ThemePlugins.resolve("unknown"));
    }

    @Test
    void resolveForSite_shouldUseThemeFromSiteConfigWhenNoExplicitThemeProvided() throws Exception {
        Files.writeString(tempDir.resolve("site-config.xml"), """
            <site>
              <title>Docs</title>
              <baseUrl>http://localhost:8080</baseUrl>
              <theme>docs</theme>
            </site>
            """);

        ThemePlugin themePlugin = ThemePlugins.resolveForSite(null, tempDir);

        assertInstanceOf(DocsThemePlugin.class, themePlugin);
    }

    @Test
    void resolveForSite_shouldPreferExplicitThemeOverSiteConfigTheme() throws Exception {
        Files.writeString(tempDir.resolve("site-config.xml"), """
            <site>
              <title>Docs</title>
              <baseUrl>http://localhost:8080</baseUrl>
              <theme>docs</theme>
            </site>
            """);

        ThemePlugin themePlugin = ThemePlugins.resolveForSite("v2", tempDir);

        assertInstanceOf(V2ThemePlugin.class, themePlugin);
    }
}
