package kmjblog.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kmjblog.converter.JsTreeNodeConverter;
import kmjblog.domain.ApiResponse;
import kmjblog.domain.Category;
import kmjblog.domain.JsTreeNodeDto;
import kmjblog.domain.Post;
import kmjblog.service.CategoryService;
import kmjblog.service.PostService;

@Controller
@RequestMapping("/blog/api/category")
public class CategoryController {
	
	/* 카테고리 서비스 */
	private final CategoryService categoryService;
	/* 게시글 서비스 */
	private final PostService postService;
	/* jsTree Converter */
	private final JsTreeNodeConverter jsTreeNodeConverter;
	
	/**
	 * CategoryController 생성자
	 * @param categoryService
	 * @param postService
	 * @param jsTreeNodeConverter
	 */
	private CategoryController(
			CategoryService categoryService, 
			PostService postService,
			JsTreeNodeConverter jsTreeNodeConverter) {
		this.categoryService = categoryService;
		this.postService = postService;
		this.jsTreeNodeConverter = jsTreeNodeConverter;
	}
	
	/**
	 * jsTree 호환 카테고리 조회
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/jstree")
	public ResponseEntity<ApiResponse<List<JsTreeNodeDto>>> getCategory(
			@RequestParam(defaultValue = "false") boolean haspost
	) {		
		// 카테고리 조회
		List<Category> categories = categoryService.selectCategoryList();
		if(categories.isEmpty()) {
			ApiResponse<List<JsTreeNodeDto>> apiResponse = new ApiResponse<List<JsTreeNodeDto>>(false, "카테고리 조회 실패", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
		}
		
		// 게시글 조회
		List<Post> posts = new ArrayList<>();
		if(haspost) {
			posts = postService.selectPostList();
		}
		
		// jsTree에 맞는 형태로 변환
		List<JsTreeNodeDto> categoryJsTreeNodes = jsTreeNodeConverter.fromCategories(categories);
		List<JsTreeNodeDto> postJsTreeNodes = jsTreeNodeConverter.fromPosts(posts);
		categoryJsTreeNodes.addAll(postJsTreeNodes);
		
		// 성공 응답
		ApiResponse<List<JsTreeNodeDto>> apiResponse = new ApiResponse<List<JsTreeNodeDto>>(true, "카테고리 조회 성공", categoryJsTreeNodes);
		return ResponseEntity.ok(apiResponse);
	}
	
	/**
	 * 카테고리 추가 (categoryId는 DB가 채번)
	 * @param request
	 * @return
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> insertCategory(
			@RequestBody Category request
	) {
		// 카테고리 추가
		int rsltCnt = categoryService.insertCategory(request);
		if(rsltCnt <= 0) {
			ApiResponse<Long> apiResponse = new ApiResponse<Long>(false, "카테고리 추가 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답 (생성된 categoryId 반환)
		ApiResponse<Long> apiResponse = new ApiResponse<Long>(true, "카테고리 추가 성공", request.getCategoryId());
		return ResponseEntity.ok(apiResponse);
	}
	
	/**
	 * 카테고리 수정
	 * @param repuest
	 * @return
	 */
	@PutMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Integer>> updateCateory(
			@PathVariable Long categoryId,
			@RequestBody Category request
	) {
		// 카테고리 수정
		int rsltCnt = categoryService.updateCategory(categoryId, request);
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "카테고리 수정 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "카테고리 수정 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 카테고리 이름만 변경
	 * @param categoryId
	 * @param request
	 * @return
	 */
	@PatchMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Integer>> renameCategory(
			@PathVariable Long categoryId,
			@RequestBody Category request
	) {
		// 카테고리 이름 변경
		int rsltCnt = categoryService.renameCategory(categoryId, request.getName());
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "카테고리 이름 변경 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "카테고리 이름 변경 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 카테고리 삭제
	 * @param categoryId
	 * @return
	 */
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Integer>> deleteCategory(
			@PathVariable Long categoryId
	) {
		// 카테고리 삭제
		int rsltCnt = categoryService.deleteCategory(categoryId);
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "카테고리 삭제 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "카테고리 삭제 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}
}
