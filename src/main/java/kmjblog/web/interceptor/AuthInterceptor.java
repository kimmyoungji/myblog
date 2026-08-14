package kmjblog.web.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import kmjblog.domain.ApiResponse;
import kmjblog.domain.LoginUser;

/**
 * 쓰기 요청(GET/HEAD를 제외한 나머지)에 대해 로그인 여부와 ADMIN 권한을 검사한다.
 * 조회(GET/HEAD)는 누구나 접근 가능하도록 통과시킨다.
 */
public class AuthInterceptor implements HandlerInterceptor {

	private static final String REQUIRED_ROLE = "ADMIN";

	private final ObjectMapper objectMapper;

	public AuthInterceptor(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		String method = request.getMethod();

		// GET/HEAD는 서버 상태를 바꾸지 않는 조회 전용 메서드다. 이 블로그는 누구나
		// 글을 읽을 수 있어야 하므로, 로그인 여부와 상관없이 통과시킨다. 로그인/권한
		// 검사는 실제로 데이터를 쓰는 POST/PUT/PATCH/DELETE에 대해서만 수행한다.
		if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) {
			return true;
		}

		HttpSession session = request.getSession(false);
		LoginUser loginUser = session == null ? null : (LoginUser) session.getAttribute(LoginUser.SESSION_KEY);

		if (loginUser == null) {
			writeError(response, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
			return false;
		}

		if (!loginUser.hasRole(REQUIRED_ROLE)) {
			writeError(response, HttpStatus.FORBIDDEN, "권한이 없습니다");
			return false;
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		// 사용하지 않음
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 사용하지 않음
	}

	private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<Void>(false, message, null)));
	}
}
