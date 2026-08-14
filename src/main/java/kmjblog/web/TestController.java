package kmjblog.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kmjblog.domain.Category;
import kmjblog.domain.Post;
import kmjblog.service.CategoryService;
import kmjblog.service.PostService;

@Controller
@RequestMapping("/test")
public class TestController {
	
	/**
	 * TestController 생성자
	 */
	private TestController() {}
	
	/**
	 * 블로그 메인 화면
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/")
	public String getTest(Model modelMap) {
		
		return "inputtest";
	}
}
