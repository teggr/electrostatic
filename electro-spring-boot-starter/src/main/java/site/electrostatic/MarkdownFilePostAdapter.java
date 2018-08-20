package site.electrostatic;

import java.time.LocalDate;

public class MarkdownFilePostAdapter implements Post {

	private MarkdownFile markdownFile;

	public MarkdownFilePostAdapter(MarkdownFile markdownFile) {
		this.markdownFile = markdownFile;
	}
	
	@Override
	public String getTitle() {
		return markdownFile.getTitle().orElse(null);
	}
	
	@Override
	public LocalDate getDate() {
		return markdownFile.getDate().orElse(LocalDate.now());
	}
	
	@Override
	public String getExcerpt() {
		return markdownFile.getExcerpt().orElse(null);
	}
	
	@Override
	public String getUrl() {		
		return "/relative/url";
	}

}
