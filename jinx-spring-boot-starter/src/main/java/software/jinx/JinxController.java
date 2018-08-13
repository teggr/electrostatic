package software.jinx;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = { "/", "/*" })
public class JinxController {

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

		model.addAttribute("twitter", new TwitterModel("jinxsoftware"));
		model.addAttribute("github", new GithubProjectModel("teggr", "jinx"));

		return layout;
	}

}