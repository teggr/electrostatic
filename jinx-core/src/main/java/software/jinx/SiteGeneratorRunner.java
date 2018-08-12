package software.jinx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SiteGeneratorRunner implements CommandLineRunner {

	private final SiteGeneratorProperties properties;

	private final TemplateEngine templateEngine;

	@Override
	public void run(String... args) throws Exception {
		log.info("Command line args {}", Arrays.asList(args));
		log.info("Properties {}", properties);

		processStatic();
		processPosts();
		processPages();

	}

	private void processStatic() throws IOException {

		// process pages
		Path staticFolder = Paths.get(properties.getSiteDirectoryLocation(), "static");

		Path outputFolder = Paths.get(properties.getOutputDirectoryLocation()).toAbsolutePath();
		Files.createDirectories(outputFolder);

		FileUtils.copyDirectory(staticFolder.toFile(), outputFolder.toFile());
	}

	private void processPages() throws IOException {
		// process pages
		Path pagesFolder = Paths.get(properties.getSiteDirectoryLocation(), "pages");

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
		Path postsFolder = Paths.get(properties.getSiteDirectoryLocation(), "posts");

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
			Path outputFile = Paths.get(properties.getOutputDirectoryLocation(), page.getPath()).toAbsolutePath();
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
		context.setVariable("twitter", new TwitterModel("jinxsoftware"));
		context.setVariable("github", new GithubProjectModel("teggr", "jinx"));

		return new Page(post, templateEngine.process(layout, context));

	}

}
