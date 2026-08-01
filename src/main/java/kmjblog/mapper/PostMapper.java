package kmjblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kmjblog.domain.Post;

@Mapper
public interface PostMapper {
	public List<Post> selectPostList();
	public Post selectPost(Long postId);
	public int insertPost(Post post);
	public int updatePost(Post post);
	public int updatePostTitle(Post post);
	public int updatePostPosition(Post post);
	public List<Post> selectSiblingPosts(@Param("parentPostId") Long parentPostId, @Param("categoryId") Long categoryId);
	public List<Post> selectPostsByCategorySubtree(@Param("categoryId") Long categoryId);
	public int updatePostSortSeq(@Param("postId") Long postId, @Param("sortSeq") int sortSeq);
	public int deletePost(Long postId);
}
