package blog.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlogController {
	
	@GetMapping("/blog")
	public String getBlog(Model modalMap) {
		return "blog";
	}
}
