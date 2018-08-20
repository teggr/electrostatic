package site.electrostatic;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import site.electrostatic.social.GithubProjectAccount;
import site.electrostatic.social.TwitterAccount;

@Controller
@RequestMapping(path = { "/", "/*" })
public class ElectroController {

	@Autowired
	private TwitterAccount twitter;

	@Autowired
	private GithubProjectAccount githubProject;

	@GetMapping
	public String get(HttpServletRequest request, Model model) throws IOException {

		String requestURI = request.getRequestURI();
		System.out.println(requestURI);
		requestURI = "/pages" + requestURI;
		if (requestURI.endsWith("/")) {
			requestURI = requestURI + "index";
		}
		System.out.println(requestURI);

		ClassPathResource classPathResource = new ClassPathResource(requestURI + ".md");

		MarkdownFile markdownFile = MarkdownReader.read(classPathResource.getInputStream());

		String layout = markdownFile.getLayout().orElse("post");

		Accounts accounts = Accounts.builder().twitter(twitter).githubProject(githubProject).build();
		accounts.getActive().stream().forEach(a -> model.addAttribute(a.getName(), a));

		model.addAttribute("title", markdownFile.getTitle());
		model.addAttribute("site", new AggregatingSiteContext(new PageContext(), new SiteConfiguration()));
		model.addAttribute("content", markdownFile.getContent());
		model.addAttribute("posts", Arrays.asList( new MarkdownFilePostAdapter( markdownFile ) ) );

		return layout;
	}

}
