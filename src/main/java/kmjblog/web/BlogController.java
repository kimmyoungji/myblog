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
@RequestMapping("/blog")
public class BlogController {
	
	/* 카테고리 서비스 */
	private final CategoryService categoryService;
	/* 게시글 서비스 */
	private final PostService postService;
	
	/**
	 * BlogController 생성자
	 * @param categoryService
	 * @param postService
	 */
	private BlogController(
			CategoryService categoryService, 
			PostService postService) {
		this.categoryService = categoryService;
		this.postService = postService;
	}
	
	/**
	 * 블로그 메인 화면
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/")
	public String getBlog(Model modelMap) {
		
		// 게시글을 포함한 카테고리 트리 조회
		List<Category> categoryTreeWithPosts = categoryService.buildCategoryTreeWithPosts();
		
		// 게시글이 있는 최상단 카테고리 아이디 가져오기
		Category frstCatHasPost = categoryService.findFrstCatHasPost(categoryTreeWithPosts);
		
		// 게시물 단건 조회
		Long frstPostId = frstCatHasPost.getPosts().get(0).getPostId();
		Post frstPost   = postService.selectPost(frstPostId);
		
		// modelMap 구성
		modelMap.addAttribute("categoryTreeWithPosts", categoryTreeWithPosts);
		modelMap.addAttribute("frstPost", frstPost);
		
		return "blog";
	}
}
