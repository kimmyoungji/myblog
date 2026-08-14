package kmjblog.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 로그인 성공 시 세션에 저장되는 사용자 정보. 비밀번호는 담지 않는다.
 */
public class LoginUser implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 세션에 저장할 때 사용하는 attribute 키 */
	public static final String SESSION_KEY = "LOGIN_USER";

	private Long userId;
	private String email;
	private List<String> roleCodes;

	public LoginUser() {}

	public LoginUser(Long userId, String email, List<String> roleCodes) {
		this.userId = userId;
		this.email = email;
		this.roleCodes = roleCodes;
	}

	public boolean hasRole(String roleCode) {
		return roleCodes != null && roleCodes.contains(roleCode);
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getRoleCodes() {
		return roleCodes;
	}
	public void setRoleCodes(List<String> roleCodes) {
		this.roleCodes = roleCodes;
	}
}
