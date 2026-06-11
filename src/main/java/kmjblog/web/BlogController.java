package kmjblog.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kmjblog.service.CategoryService;

@Controller
public class BlogController {
	
	private final CategoryService categoryService;
	
	private BlogController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping("/blog")
	public String getBlog(Model modelMap) {
		
		List<Map<String, Object>> categoryList = categoryService.selectCategoryList();
		
		modelMap.addAttribute("categoryList", categoryList);
		
		return "blog";
	}
}
