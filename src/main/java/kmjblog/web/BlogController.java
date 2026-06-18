package kmjblog.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kmjblog.domain.Category;
import kmjblog.service.CategoryService;

@Controller
@RequestMapping("/blog")
public class BlogController {
	
	private final CategoryService categoryService;
	
	private BlogController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping("/")
	public String getBlog(Model modelMap) {
		
		List<Category> categoryTree = categoryService.buildCategoryTree();
		
		modelMap.addAttribute("categoryTree", categoryTree);
		
		System.out.println(categoryTree.toString());
		
		return "blog";
	}
}
