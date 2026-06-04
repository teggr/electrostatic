package site.electrostatic.theme.pages;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import site.electrostatic.docs.DocsMarkdownUrlResolver;
import site.electrostatic.docs.DocsSection;
import site.electrostatic.docs.DocsSectionConfig;
import site.electrostatic.engine.Page;
import site.electrostatic.engine.RenderModel;
import site.electrostatic.utils.Utils;
import j2html.tags.DomContent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.iff;
import static j2html.TagCreator.ol;
import static j2html.TagCreator.p;
import static j2html.TagCreator.li;
import static j2html.TagCreator.rawHtml;

public class DocsLandingPage {

  public static Page create(Path sourceDirectory) {
    LandingContent landingContent = loadLandingContent(sourceDirectory);

    Map<String, List<String>> data = new LinkedHashMap<>();
    data.put("layout", List.of("default"));
    data.put("title", List.of(resolvePageTitle(landingContent)));
    if (landingContent != null && landingContent.description() != null && !landingContent.description().isBlank()) {
      data.put("description", List.of(landingContent.description()));
    }

    return Page.builder()
        .path("/index.html")
        .data(data)
        .renderFunction(renderModel -> render(renderModel, landingContent))
        .build();
  }

  private static DomContent render(RenderModel renderModel, LandingContent landingContent) {
    List<DocsSection> sections = DocsSectionConfig.fromSite(renderModel.getContext().getSite());

    return div()
        .withClass("docs-landing")
        .with(
            landingContent != null ? renderLandingContent(renderModel, landingContent) : renderDefaultIntro(renderModel),
            div()
                .withClass("docs-section-grid")
                .with(
                    each(sections, section ->
                        div()
                            .withClass("docs-section-card")
                            .with(
                                h2().withText(section.label()),
                                                                p().withText(describeSection(section.label())),
                                a()
                                    .withHref(Utils.relativeUrl(section.indexPath()))
                                                                        .withText("Browse")
                            )
                    )
                )
        );
  }

  private static String resolvePageTitle(LandingContent landingContent) {
    if (landingContent != null && landingContent.title() != null && !landingContent.title().isBlank()) {
      return landingContent.title();
    }

    return "Documentation";
  }

  private static DomContent renderLandingContent(RenderModel renderModel, LandingContent landingContent) {
    String docsBasePath = DocsMarkdownUrlResolver.docsBasePath(renderModel.getContext().getSite().getBaseUrl());
    landingContent.document().accept(new AbstractVisitor() {
      @Override
      public void visit(Image image) {
        image.setDestination(DocsMarkdownUrlResolver.resolve(image.getDestination(), docsBasePath));
        super.visit(image);
      }

      @Override
      public void visit(Link link) {
        link.setDestination(DocsMarkdownUrlResolver.resolve(link.getDestination(), docsBasePath));
        super.visit(link);
      }
    });

    HtmlRenderer renderer = HtmlRenderer.builder()
        .extensions(List.of(HeadingAnchorExtension.create()))
        .build();

    return div()
        .withClass("docs-landing-intro")
        .with(
            iff(
                landingContent.title() != null && !landingContent.title().isBlank(),
                h1().withText(landingContent.title())
            ),
            rawHtml(renderer.render(landingContent.document()))
        );
  }

  private static DomContent renderDefaultIntro(RenderModel renderModel) {
    return div()
        .withClass("docs-landing-intro")
        .with(
            h1().withText(renderModel.getContext().getSite().getTitle()),
            p().withText("Build and publish documentation sites with Electrostatic."),
            p().withText("Use this reference to install the toolchain, choose a theme, and configure plugins."),
            h2().withText("Quick Start"),
            ol().with(
                li().withText("Install via JBang or Maven plugin."),
                li().withText("Initialize a site with your preferred theme."),
                li().withText("Write content, then run generate or serve.")
            )
        );
  }

  private static LandingContent loadLandingContent(Path sourceDirectory) {
    Path landingPage = sourceDirectory.resolve("_index.md");
    if (!Files.exists(landingPage)) {
      return null;
    }

    try {
      String markdown = Files.readString(landingPage);
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

      return new LandingContent(
          firstValue(frontmatter, "title"),
          firstValue(frontmatter, "description"),
          document
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse docs landing page " + landingPage + ": " + e.getMessage(), e);
    }
  }

  private static String firstValue(Map<String, List<String>> map, String key) {
    List<String> values = map.get(key);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

  private static String describeSection(String label) {
    return switch (label.toLowerCase()) {
      case "installation" -> "Set up Electrostatic for local development and CI builds.";
      case "guides" -> "Follow practical workflows for themes and day-to-day authoring.";
      case "plugins" -> "Understand built-in plugins and when to use each one.";
      default -> "Explore this section for " + label.toLowerCase() + " docs.";
    };
  }

  private record LandingContent(String title, String description, Node document) {
  }

}
