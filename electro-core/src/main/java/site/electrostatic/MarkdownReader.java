package site.electrostatic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownReader {

	public static MarkdownFile read(Path path) {

		try {
			String input = new String(Files.readAllBytes(path), Charset.defaultCharset());
			StringReader reader = new StringReader(input);
			return read(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	public static MarkdownFile read(InputStream in) {

		return read(new InputStreamReader(in));

	}

	private static MarkdownFile read(Reader reader) {
		try {
			List<Extension> extensions = Arrays.asList(YamlFrontMatterExtension.create());
			Parser parser = Parser.builder().extensions(extensions).build();
			HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
			YamlFrontMatterVisitor frontMatterVisitor = new YamlFrontMatterVisitor();
			Node document = parser.parseReader(reader);

			document.accept(frontMatterVisitor);
			System.out.println(renderer.render(document)); // "<p>This is
			// <em>Sparta</em></p>\n"
			System.out.println(frontMatterVisitor.getData()); // title

			return new MarkdownFile(frontMatterVisitor.getData(), renderer.render(document));

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
