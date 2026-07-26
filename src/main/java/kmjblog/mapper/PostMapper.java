package kmjblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kmjblog.domain.Post;

@Mapper
public interface PostMapper {
	public List<Post> selectPostList();
	public Post selectPost(Long postId);
	public int insertPost(Post post);
	public int updatePost(Post post);
	public int renamePost(Post post);
	public int deletePost(Long postId);
}
