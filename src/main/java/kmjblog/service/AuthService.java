package kmjblog.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kmjblog.domain.LoginUser;
import kmjblog.domain.User;
import kmjblog.mapper.UserMapper;

@Service
public class AuthService {

	private final UserMapper userMapper;
	private final BCryptPasswordEncoder bcryptPasswordEncoder;

	public AuthService(UserMapper userMapper, BCryptPasswordEncoder bcryptPasswordEncoder) {
		this.userMapper = userMapper;
		this.bcryptPasswordEncoder = bcryptPasswordEncoder;
	}

	/**
	 * 이메일/비밀번호로 로그인을 시도한다.
	 * @param email
	 * @param rawPassword
	 * @return 로그인 성공 시 세션에 저장할 LoginUser, 실패 시 null
	 */
	public LoginUser login(String email, String rawPassword) {
		User user = userMapper.selectUserByEmail(email);
		if (user == null) {
			return null;
		}

		if (!bcryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
			return null;
		}

		List<String> roleCodes = userMapper.selectRoleCodesByUserId(user.getUserId());
		return new LoginUser(user.getUserId(), user.getEmail(), roleCodes);
	}
}
