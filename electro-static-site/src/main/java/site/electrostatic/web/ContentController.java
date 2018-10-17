package site.electrostatic.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{path}")
public class ContentController {
	
	@Autowired
	private ContentService contentService;

	@GetMapping
	public String get( @PathVariable("path") String path, Model model ) {
		
		// assume a site model has been mapped out in memory
		// we can just fetch the content for the relative path
		// put the content/metadata in the content
		// derive the template from the source/config
		
		model.addAttribute("content", "some text");
		return "list";
	}
	
}
