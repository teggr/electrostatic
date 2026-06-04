package site.electrostatic.markdown;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.parser.Parser;

import java.util.List;

public final class MarkdownParserFactory {

  private static final List<Extension> EXTENSIONS = List.of(
      YamlFrontMatterExtension.create(),
      HeadingAnchorExtension.create()
  );

  private MarkdownParserFactory() {
  }

  public static Parser create() {
    return Parser.builder()
        .extensions(EXTENSIONS)
        .build();
  }
}