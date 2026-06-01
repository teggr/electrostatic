package site.electrostatic.theme.docs;

import site.electrostatic.content.staticfiles.ClasspathFilesPlugin;
import site.electrostatic.content.staticfiles.StaticFilesPlugin;
import site.electrostatic.docs.DocsCollectionPlugin;
import site.electrostatic.docs.DocsSection;
import site.electrostatic.docs.DocsSectionConfig;
import site.electrostatic.engine.ContentModel;
import site.electrostatic.engine.Layout;
import site.electrostatic.plugins.ContentRenderPlugin;
import site.electrostatic.plugins.ContentTypePlugin;
import site.electrostatic.plugins.InitializationPlugin;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.plugins.ThemePlugin;
import site.electrostatic.site.Site;
import site.electrostatic.theme.layouts.DefaultLayout;
import site.electrostatic.theme.layouts.PageLayout;
import site.electrostatic.theme.pages.DocsLandingPage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class DocsThemePlugin implements ContentTypePlugin, ContentRenderPlugin, ThemePlugin, InitializationPlugin {

  public static DocsThemePlugin create() {
    return new DocsThemePlugin();
  }

  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {
    contentModel.addPage(DocsLandingPage.create());
  }

  @Override
  public void loadLayout(Map<String, Layout> layouts) {
    layouts.put("default", DefaultLayout.create());
    layouts.put("page", PageLayout.create());
  }

  @Override
  public void registerPlugins() {
    Plugins.initializationPlugins.add(this);
    Plugins.contentTypePlugins.add(this);
    Plugins.contentRenderPlugins.add(this);

    DocsCollectionPlugin.create().registerPlugins();
    StaticFilesPlugin.create("_static").registerPlugins();

    ClasspathFilesPlugin.create(List.of(
            "theme/default/css/main.css",
            "theme/default/css/style.css",
            "theme/default/images/minima-social-icons.svg"
        ))
        .registerPlugins();
  }

  @Override
  public void initialize(Path sourceDirectory) {
    try {
      for (DocsSection section : DocsSectionConfig.defaults()) {
        Path sectionDirectory = sourceDirectory.resolve(section.folderName());
        Files.createDirectories(sectionDirectory);
        writeStarterDocIfMissing(section, sectionDirectory);
      }

      Path siteConfig = sourceDirectory.resolve("site-config.xml");
      if (Files.notExists(siteConfig)) {
        Files.writeString(siteConfig, """
            <site>
              <title>My Electrostatic Docs</title>
              <baseUrl>http://localhost:8080</baseUrl>
              <theme>docs</theme>
              <description>Project documentation</description>
              <author>
                <name>Your Name</name>
              </author>
              <docsSections>installation,guides,plugins</docsSections>
              <docsSectionLabels>installation=Installation,guides=Guides,plugins=Plugins</docsSectionLabels>
            </site>
            """);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize docs theme content", e);
    }
  }

  private void writeStarterDocIfMissing(DocsSection section, Path sectionDirectory) throws Exception {
    Path starterDoc = switch (section.key()) {
      case "installation" -> sectionDirectory.resolve("getting-started.md");
      case "guides" -> sectionDirectory.resolve("first-guide.md");
      case "plugins" -> sectionDirectory.resolve("plugin-overview.md");
      default -> sectionDirectory.resolve("overview.md");
    };

    if (Files.exists(starterDoc)) {
      return;
    }

    Files.writeString(starterDoc, """
        ---
        title: %s
        description: Starter page for the %s section.
        order: 1
        ---

        Add your %s documentation here.
        """.formatted(section.label(), section.label().toLowerCase(), section.label().toLowerCase()));
  }

}
