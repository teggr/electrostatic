package software.jinx;

import java.nio.charset.Charset;

public class Page {

	private final MarkdownFile markdownFile;
	private final String render;

	public Page(MarkdownFile post, String render) {
		this.markdownFile = post;
		this.render = render;
	}

	public String getPath() {
		return markdownFile.getTitle().orElse("index").toLowerCase().trim().replaceAll(" ", "-") + ".html";
	}

	public byte[] getRender() {
		return render.getBytes(Charset.defaultCharset());
	}

}
