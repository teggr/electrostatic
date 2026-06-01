package site.electrostatic.docs;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import site.electrostatic.engine.ContentModel;
import site.electrostatic.engine.Page;
import site.electrostatic.engine.RenderModel;
import site.electrostatic.plugins.ContentTypePlugin;
import site.electrostatic.plugins.InitializationPlugin;
import site.electrostatic.plugins.Plugins;
import site.electrostatic.site.Site;
import j2html.tags.DomContent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.iff;
import static j2html.TagCreator.li;
import static j2html.TagCreator.p;
import static j2html.TagCreator.ul;

@Slf4j
public class DocsCollectionPlugin implements ContentTypePlugin, InitializationPlugin {

  private final Map<String, List<DocsEntry>> entriesBySection = new HashMap<>();

  public static DocsCollectionPlugin create() {
    return new DocsCollectionPlugin();
  }

  @Override
  public void loadContent(Path sourceDirectory, Site site, ContentModel contentModel) {
    entriesBySection.clear();

    List<DocsSection> sections = DocsSectionConfig.fromSite(site);
    for (DocsSection section : sections) {
      List<DocsEntry> entries = loadSectionEntries(sourceDirectory, section);
      entriesBySection.put(section.key(), entries);

      entries.forEach(contentModel::add);
      contentModel.addPage(createSectionIndexPage(section, entries));
    }
  }

  @Override
  public void initialize(Path sourceDirectory) {
    List<DocsSection> sections = DocsSectionConfig.defaults();
    for (DocsSection section : sections) {
      try {
        Files.createDirectories(sourceDirectory.resolve(section.folderName()));
      } catch (Exception e) {
        throw new RuntimeException("Failed to initialize docs section directory " + section.folderName(), e);
      }
    }
  }

  public void registerPlugins() {
    Plugins.initializationPlugins.add(this);
    Plugins.contentTypePlugins.add(this);
  }

  private List<DocsEntry> loadSectionEntries(Path sourceDirectory, DocsSection section) {
    Path sectionDirectory = sourceDirectory.resolve(section.folderName());
    if (Files.notExists(sectionDirectory)) {
      return List.of();
    }

    List<DocsEntry> entries = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(sectionDirectory)) {
      paths
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".md"))
          .map(path -> readEntry(section, path))
          .filter(entry -> entry != null)
          .forEach(entries::add);
    } catch (Exception e) {
      throw new RuntimeException("Failed loading docs entries from " + sectionDirectory, e);
    }

    return entries.stream()
        .sorted(Comparator
            .comparingInt(DocsEntry::getOrder)
            .thenComparing(DocsEntry::getTitle, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private DocsEntry readEntry(DocsSection section, Path path) {
    try {
      String markdown = Files.readString(path);

      List<Extension> extensions = List.of(
          YamlFrontMatterExtension.create(),
          HeadingAnchorExtension.create()
      );

      Parser parser = Parser.builder()
          .extensions(extensions)
          .build();

      Node document = parser.parse(markdown);

      YamlFrontMatterVisitor frontMatterVisitor = new YamlFrontMatterVisitor();
      document.accept(frontMatterVisitor);

      Map<String, List<String>> frontmatter = new LinkedHashMap<>(frontMatterVisitor.getData());

      String filename = path.getFileName().toString();
      String defaultSlug = filename.substring(0, filename.length() - 3);
      String slug = firstValue(frontmatter, "slug");
      if (slug == null || slug.isBlank()) {
        slug = slugify(defaultSlug);
      }

      String title = firstValue(frontmatter, "title");
      if (title == null || title.isBlank()) {
        title = toTitle(defaultSlug);
      }

      String description = firstValue(frontmatter, "description");

      int order = Integer.MAX_VALUE;
      String orderRaw = firstValue(frontmatter, "order");
      if (orderRaw != null && !orderRaw.isBlank()) {
        try {
          order = Integer.parseInt(orderRaw.trim());
        } catch (NumberFormatException ignored) {
          log.warn("Invalid docs order '{}' in {}", orderRaw, path);
        }
      }

      frontmatter.putIfAbsent("layout", List.of("page"));
      frontmatter.put("title", List.of(title));

      String url = section.basePath() + slug + ".html";
      return new DocsEntry(section, slug, title, description, order, url, document, frontmatter);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse docs entry: " + path, e);
    }
  }

  private Page createSectionIndexPage(DocsSection section, List<DocsEntry> entries) {
    return Page.builder()
        .path(section.indexPath())
        .includeMenu(true)
        .data(Map.of(
            "layout", List.of("page"),
            "title", List.of(section.label()),
            "list_title", List.of(section.label())
        ))
        .renderFunction(renderModel -> renderSectionIndex(renderModel, section, entries))
        .build();
  }

  private DomContent renderSectionIndex(RenderModel renderModel, DocsSection section, List<DocsEntry> entries) {
    return div()
        .withClass("docs-section-index")
        .with(
            h2().withText(section.label()),
            iff(
                entries.isEmpty(),
                p().withText("No entries yet.")
            ),
            iff(
                !entries.isEmpty(),
                ul().with(
                    each(entries, entry ->
                        li().with(
                            a().withHref(entry.getUrl()).withText(entry.getTitle()),
                            iff(
                                entry.getDescription() != null && !entry.getDescription().isBlank(),
                                p().withText(entry.getDescription())
                            )
                        )
                    )
                )
            )
        );
  }

  private String firstValue(Map<String, List<String>> map, String key) {
    List<String> values = map.get(key);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

  private String slugify(String value) {
    String normalized = value.toLowerCase(Locale.ROOT)
        .replaceAll("[^a-z0-9\\s-]", "")
        .trim()
        .replaceAll("\\s+", "-")
        .replaceAll("-+", "-");
    return normalized.isBlank() ? "page" : normalized;
  }

  private String toTitle(String slug) {
    StringBuilder label = new StringBuilder();
    boolean capitalize = true;
    for (char ch : slug.toCharArray()) {
      if (ch == '-' || ch == '_') {
        label.append(' ');
        capitalize = true;
      } else if (capitalize) {
        label.append(Character.toUpperCase(ch));
        capitalize = false;
      } else {
        label.append(ch);
      }
    }
    return label.toString();
  }

}
