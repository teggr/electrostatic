package site.electrostatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SiteGenerator {

	private final SiteGeneratorConfiguration configuration;

	private final TemplateEngine templateEngine;

	private final Accounts accounts;

	public void run() throws Exception {

		log.info("Properties {}", configuration);

		processStatic();
		processPosts();
		processPages();

	}

	private void processStatic() throws IOException {

		// process pages
		Path staticFolder = Paths.get(configuration.getBaseDirectory().getAbsolutePath(), "static");

		Path outputFolder = Paths.get(configuration.getOutputDirectory().getAbsolutePath()).toAbsolutePath();
		Files.createDirectories(outputFolder);

		FileUtils.copyDirectory(staticFolder.toFile(), outputFolder.toFile());
	}

	private void processPages() throws IOException {
		// process pages
		Path pagesFolder = Paths.get(configuration.getBaseDirectory().getAbsolutePath(), "pages");

		// get all posts
		List<MarkdownFile> posts = Files.walk(pagesFolder).filter(Files::isRegularFile).map(MarkdownReader::read)
				.collect(Collectors.toList());

		// get the template engine
		List<Page> pages = posts.stream().map(this::transform).collect(Collectors.toList());

		// process each against the named template
		pages.stream().forEachOrdered(this::writePage);
	}

	private void processPosts() throws IOException {
		// process posts
		Path postsFolder = Paths.get(configuration.getBaseDirectory().getAbsolutePath(), "posts");

		// get all posts
		List<MarkdownFile> posts = Files.walk(postsFolder).filter(Files::isRegularFile).map(MarkdownReader::read)
				.collect(Collectors.toList());

		// get the template engine
		List<Page> pages = posts.stream().map(this::transform).collect(Collectors.toList());

		// process each against the named template
		pages.stream().forEachOrdered(this::writePage);
	}

	public void writePage(Page page) {

		try {
			Path outputFile = Paths.get(configuration.getOutputDirectory().getAbsolutePath(), page.getPath())
					.toAbsolutePath();
			Files.createDirectories(outputFile.getParent());
			Files.write(outputFile, page.getRender());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Page transform(MarkdownFile post) {

		String layout = post.getLayout().orElse("post");

		log.info("layout {}", layout);

		Context context = new Context();
		context.setVariable("post", post);
		accounts.getActive().stream().forEach(a -> context.setVariable(a.getName(), a));

		return new Page(post, templateEngine.process(layout, context));

	}

}
