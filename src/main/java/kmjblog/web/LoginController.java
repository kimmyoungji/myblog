package kmjblog.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kmjblog.domain.LoginUser;

@Controller
@RequestMapping("/login")
public class LoginController {

	/**
	 * 로그인 화면
	 * @return
	 */
	@GetMapping("")
	public String getLogin(HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
	    LoginUser loginUser = session == null ? null 
	        : (LoginUser) session.getAttribute(LoginUser.SESSION_KEY);

	    if (loginUser != null) {
	        return "redirect:/blog/";
	    }
	    
		return "login";
	}
}
