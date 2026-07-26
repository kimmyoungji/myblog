package kmjblog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kmjblog.domain.Post;
import kmjblog.mapper.PostMapper;

@Service
public class PostService {
	
	PostMapper postMapper;
	
	public PostService (PostMapper postMapper) {
		this.postMapper = postMapper;
	}
	
	/**
	 * 게시물 단건 조회
	 * @param postId
	 * @return
	 */
	public Post selectPost(Long postId) {
		return postMapper.selectPost(postId);
	}
	
	/**
	 * 게시물 목록 조회 
	 * @return
	 */
	public List<Post> selectPostList() {
		return postMapper.selectPostList();
	}
	
	/**
	 * 게시물 트리 조회 
	 * @return
	 */
	public List<Post> buildPostTree() {
		
		List<Post> postList = postMapper.selectPostList();
		List<Post> rootPosts = new ArrayList<Post>();
		
		// {postId:Post객체} 형태의 postMap 구성하기
		Map<Long,Post> postMap = new HashMap<Long, Post>();
		for(Post post : postList) {
			postMap.put(post.getPostId(), post);
		}
		
		// postMap을 순회
		for(Post post : postList) {
			
			// parentPostId가 없을 경우
			Long parentPostId = post.getParentPostId();
			if(parentPostId == null) {
				rootPosts.add(post);
				continue;
			}

			// parentPostId가 없을 경우
			Post parentPost = postMap.get(parentPostId);
			if(parentPost != null) { // parentPost 객체가 있는지 확인
				parentPost.getChildren().add(post);
			} else {
				rootPosts.add(post); // parentPost 객체가 없을 경우 루트 게시글로 등록
			}
		}
		
		return rootPosts;
	}
	
	/**
	 * 게시물 추가 (postId는 DB가 채번)
	 * @param post
	 * @return
	 */
	public int insertPost(Post post) {
		post.setAuthorId((long) 9999);
		return postMapper.insertPost(post);
	}

	/**
	 * 게시물 수정
	 * @param postId
	 * @param post
	 * @return
	 */
	public int updatePost(Long postId, Post post) {
		post.setPostId(postId);
		return postMapper.updatePost(post);
	}
	
	/**
	 * 게시물 제목만 변경
	 * @param postId
	 * @param title
	 * @return
	 */
	public int renamePost(Long postId, String title) {
		Post post = new Post();
		post.setPostId(postId);
		post.setTitle(title);
		return postMapper.renamePost(post);
	}

	/**
	 * 게시물 삭제
	 * @param postId
	 * @return
	 */
	public int deletePost(Long postId) {
		return postMapper.deletePost(postId);
	} 
}