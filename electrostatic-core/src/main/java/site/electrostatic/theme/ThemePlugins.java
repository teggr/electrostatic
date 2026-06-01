package site.electrostatic.theme;

import site.electrostatic.plugins.ThemePlugin;
import site.electrostatic.site.Site;
import site.electrostatic.site.SitePlugin;
import site.electrostatic.theme.docs.DocsThemePlugin;
import site.electrostatic.theme.v2.V2ThemePlugin;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class ThemePlugins {

  private static final List<String> AVAILABLE_THEME_IDS = List.of("default", "docs", "v2");

  private ThemePlugins() {
  }

  public static List<String> availableThemeIds() {
    return AVAILABLE_THEME_IDS;
  }

  public static ThemePlugin resolve(String themeId) {
    String normalized = themeId == null ? "default" : themeId.trim().toLowerCase(Locale.ROOT);
    return switch (normalized) {
      case "", "default" -> DefaultThemePlugin.create();
      case "docs" -> DocsThemePlugin.create();
      case "v2" -> V2ThemePlugin.create();
      default -> throw new IllegalArgumentException("Unknown theme: " + themeId + ". Supported values: " + String.join(", ", AVAILABLE_THEME_IDS));
    };
  }

  public static ThemePlugin resolveForSite(String explicitThemeId, Path inputDirectory) {
    if (hasText(explicitThemeId)) {
      return resolve(explicitThemeId);
    }

    if (inputDirectory != null) {
      try {
        Site site = SitePlugin.loadFromFile(inputDirectory);
        if (site != null && hasText(site.getTheme())) {
          return resolve(site.getTheme());
        }
      } catch (Exception ignored) {
        // Fallback to default when site config is missing or cannot be parsed.
      }
    }

    return resolve("default");
  }

  private static boolean hasText(String value) {
    return value != null && !value.trim().isEmpty();
  }

}
