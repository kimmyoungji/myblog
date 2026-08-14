package kmjblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kmjblog.domain.User;

@Mapper
public interface UserMapper {
	public User selectUserByEmail(String email);
	public List<String> selectRoleCodesByUserId(Long userId);
}
