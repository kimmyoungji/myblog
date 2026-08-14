package kmjblog.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kmjblog.domain.ApiResponse;
import kmjblog.domain.LoginUser;
import kmjblog.domain.User;
import kmjblog.service.AuthService;

@Controller
@RequestMapping("/blog/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	/**
	 * 로그인
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<ApiResponse<LoginUser>> login(
			@RequestBody User request,
			HttpServletRequest httpRequest
	) {
		LoginUser loginUser = authService.login(request.getEmail(), request.getPassword());
		if (loginUser == null) {
			ApiResponse<LoginUser> apiResponse = new ApiResponse<LoginUser>(false, "이메일 또는 비밀번호가 올바르지 않습니다", null);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		}

		httpRequest.getSession(true).setAttribute(LoginUser.SESSION_KEY, loginUser);

		ApiResponse<LoginUser> apiResponse = new ApiResponse<LoginUser>(true, "로그인 성공", loginUser);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 로그아웃
	 * @param httpRequest
	 * @return
	 */
	@PostMapping("/logout")
	@ResponseBody
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest) {
		HttpSession session = httpRequest.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		ApiResponse<Void> apiResponse = new ApiResponse<Void>(true, "로그아웃 성공", null);
		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 현재 로그인한 사용자 조회
	 * @param httpRequest
	 * @return
	 */
	@GetMapping("/me")
	@ResponseBody
	public ResponseEntity<ApiResponse<LoginUser>> me(HttpServletRequest httpRequest) {
		HttpSession session = httpRequest.getSession(false);
		LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute(LoginUser.SESSION_KEY);

		if (loginUser == null) {
			ApiResponse<LoginUser> apiResponse = new ApiResponse<LoginUser>(false, "로그인 상태가 아닙니다", null);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		}

		ApiResponse<LoginUser> apiResponse = new ApiResponse<LoginUser>(true, "조회 성공", loginUser);
		return ResponseEntity.ok(apiResponse);
	}
}
