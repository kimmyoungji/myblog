package kmjblog.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public BlogController(
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
		
		// modelMap 구성
		modelMap.addAttribute("categoryTreeWithPosts", categoryTreeWithPosts);
		//modelMap.addAttribute("frstPost", frstPost);
		
		return "blog";
	}

	/**
	 * 블로그 post-list 화면
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/category/{categoryId}")
	public String getPostListPage(@PathVariable("categoryId") Long categoryId, Model modelMap) {
		
		// 게시글을 포함한 카테고리 트리 조회
		List<Category> categoryTreeWithPosts = categoryService.buildCategoryTreeWithPosts();
		
		// modelMap 구성
		modelMap.addAttribute("categoryTreeWithPosts", categoryTreeWithPosts);
		modelMap.addAttribute("selectedCategory", categoryId);
		
		return "blog";
	}

	/**
	 * 블로그 post-list 화면
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/post/{postId}")
	public String getPostDetailPage(@PathVariable("postId") Long postId, Model modelMap) {
		
		// 게시글을 포함한 카테고리 트리 조회
		List<Category> categoryTreeWithPosts = categoryService.buildCategoryTreeWithPosts();
		
		// modelMap 구성
		modelMap.addAttribute("categoryTreeWithPosts", categoryTreeWithPosts);
		modelMap.addAttribute("selectedPost", postId);
		
		return "blog";
	}

	/**
	 * 블로그 post-detail 화면
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/{categoryId}/{postId}")
	public String getPostPage(
		@PathVariable("categoryId") Long categoryId,
		@PathVariable("postId") Long postId, 
		Model modelMap) {
		
		// 게시글을 포함한 카테고리 트리 조회
		List<Category> categoryTreeWithPosts = categoryService.buildCategoryTreeWithPosts();
		
		// modelMap 구성
		modelMap.addAttribute("categoryTreeWithPosts", categoryTreeWithPosts);
		modelMap.addAttribute("selectedCategory", categoryId);
		modelMap.addAttribute("selectedPost", postId);
		
		return "blog";
	}

}
