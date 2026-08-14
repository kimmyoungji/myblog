package kmjblog.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.ResponseBody;

import kmjblog.domain.ApiResponse;
import kmjblog.domain.LoginUser;
import kmjblog.domain.Post;
import kmjblog.service.PostService;

@Controller
@RequestMapping("/blog/api/post")
public class PostController {
	
	/* 게시글 서비스 */
	private final PostService postService;
	
	/**
	 * CategoryController 생성자
	 * @param postService
	 */
	private PostController(
			PostService postService) {
		this.postService = postService;
	}
	
	/**
	 * 카테고리에 직속된 게시글 목록 조회
	 * @param categoryId
	 * @return
	 */
	@ResponseBody
	@GetMapping
	public ResponseEntity<ApiResponse<List<Post>>> getPostsByCategory(
			@RequestParam Long categoryId
	){
		// 카테고리 직속 게시글 목록 조회
		List<Post> posts = postService.selectPostsByCategorySubtree(categoryId);

		// 성공 응답
		ApiResponse<List<Post>> apiResponse = new ApiResponse<List<Post>>(true, "게시글 목록 조회 성공", posts);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 게시글 단건 조회
	 * @param postId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/{postId}")
	public ResponseEntity<ApiResponse<Post>> getPost(
			@PathVariable Long postId
	){
		// 게시물 단건 조회
		Post post = postService.selectPost(postId);
		
		// 조회된 게시물이 없을 경우 실패 응답
		if(post == null) {
			ApiResponse<Post> apiResponse = new ApiResponse<Post>(false, "게시글 조회 실패", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
		}
		
		// 성공 응답
		ApiResponse<Post> apiResponse = new ApiResponse<Post>(true, "게시글 조회 성공", post);
		return ResponseEntity.ok(apiResponse);
	}
	
	/**
	 * 게시물 추가 (postId는 DB가 채번)
	 * @param request
	 * @return
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> insertPost(
			@RequestBody Post request,
			HttpServletRequest httpRequest
	) {
		// 작성자 = 현재 로그인한 사용자. AuthInterceptor가 이 경로의 쓰기 요청은
		// 이미 로그인/ADMIN 권한을 검증했으므로 세션에 LOGIN_USER가 있는 게 보장되지만,
		// 이 컨트롤러가 다른 경로로도 호출될 가능성에 대비해 방어적으로 한 번 더 확인한다.
		HttpSession session = httpRequest.getSession(false);
		LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute(LoginUser.SESSION_KEY);
		if (loginUser == null) {
			ApiResponse<Long> apiResponse = new ApiResponse<Long>(false, "로그인이 필요합니다", null);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		}
		request.setAuthorId(loginUser.getUserId());

		// 게시물 추가
		int rsltCnt = postService.insertPost(request);
		if(rsltCnt <= 0) {
			ApiResponse<Long> apiResponse = new ApiResponse<Long>(false, "게시물 추가 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답 (생성된 postId 반환)
		ApiResponse<Long> apiResponse = new ApiResponse<Long>(true, "게시물 추가 성공", request.getPostId());
		return ResponseEntity.ok(apiResponse);
	}
	
	/**
	 * 게시물 수정
	 * @param repuest
	 * @return
	 */
	@PutMapping("/{postId}")
	public ResponseEntity<ApiResponse<Integer>> updatePost(
			@PathVariable Long postId,
			@RequestBody Post request
	) throws IOException {
		// 카테고리 수정
		int rsltCnt = postService.updatePost(postId, request);
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "게시물 수정 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}
		
		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "게시물 수정 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 게시물 제목만 변경
	 * @param postId
	 * @param request
	 * @return
	 */
	@PatchMapping("/{postId}/rename")
	public ResponseEntity<ApiResponse<Integer>> renamePost(
			@PathVariable Long postId,
			@RequestBody Post request
	) {
		// 게시물 제목 변경
		int rsltCnt = postService.renamePost(postId, request.getTitle());
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "게시물 제목 변경 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "게시물 제목 변경 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}
	
	/**
	 * 게시물 이동
	 * @param postId
	 * @param request
	 * @return
	 */
	@PatchMapping("/{postId}/relocate")
	public ResponseEntity<ApiResponse<Integer>> relocatePost(
			@PathVariable Long postId,
			@RequestBody Post request
	) {
		// 게시물 제목 변경
		int rsltCnt = postService.relocatePost(postId, request);
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "게시물 이동 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "게시물 이동 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 게시물 삭제
	 * @param postId
	 * @return
	 */
	@DeleteMapping("/{postId}")
	public ResponseEntity<ApiResponse<Integer>> deletePost(
			@PathVariable Long postId
	) throws IOException {
		// 게시물 삭제
		int rsltCnt = postService.deletePost(postId);
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "게시물 삭제 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}
		
		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "게시물 삭제 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 게시물 일괄삭제 (하위 게시물이 있으면 함께 삭제)
	 * @param postIds
	 * @return
	 */
	@ResponseBody
	@DeleteMapping
	public ResponseEntity<ApiResponse<Integer>> deletePosts(
			@RequestBody List<Long> postIds
	) throws IOException {
		// 예외처리: 삭제 대상이 없는 경우
		if(postIds == null || postIds.isEmpty()) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "삭제할 게시물이 없습니다", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 게시물 일괄삭제
		int rsltCnt = postService.deletePosts(postIds);
		if(rsltCnt <= 0) {
			ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(false, "게시물 일괄삭제 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}

		// 성공 응답
		ApiResponse<Integer> apiResponse = new ApiResponse<Integer>(true, "게시물 일괄삭제 성공", rsltCnt);
		return ResponseEntity.ok(apiResponse);
	}
}
