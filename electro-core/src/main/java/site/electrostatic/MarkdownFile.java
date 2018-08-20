package site.electrostatic;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.ToString;

@ToString
public class MarkdownFile {

	private final Map<String, List<String>> data;
	private final String render;

	public MarkdownFile(Map<String, List<String>> data, String render) {
		this.data = data;
		this.render = render;
	}

	public Optional<String> getTitle() {
		return data.getOrDefault("title", Collections.emptyList()).stream().findFirst();
	}

	public Optional<String> getLayout() {
		return data.getOrDefault("layout", Collections.emptyList()).stream().findFirst();
	}

	public String getContent() {
		return render;
	}

	public Optional<LocalDate> getDate() {

		return Optional.of(LocalDate.now());
	}

	public Optional<String> getExcerpt() {
		return Optional.of("a short piece of text");
	}

}
