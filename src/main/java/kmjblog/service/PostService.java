package kmjblog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
	 * 게시물 이동 (부모/순서가 바뀌면 형제 게시물들의 SORT_SEQ도 함께 재정렬)
	 * @param postId
	 * @param post
	 * @return
	 */
	public int relocatePost(Long postId, Post post) {

		// 이동 전 상태 조회 (이전 형제 그룹을 알아야 재정렬 대상이 정해짐)
		Post current = postMapper.selectPost(postId);
		if(current == null) {
			return 0;
		}

		Long oldParentPostId = current.getParentPostId();
		Long oldCategoryId = current.getCategoryId();
		Long newParentPostId = post.getParentPostId();
		Long newCategoryId = post.getCategoryId();

		// 형제 그룹(부모게시물 또는 최상위일 경우 카테고리)이 바뀌었는지 판단
		boolean sameGroup = Objects.equals(oldParentPostId, newParentPostId)
				&& (oldParentPostId != null || Objects.equals(oldCategoryId, newCategoryId));

		// 그룹이 바뀌었다면 이전 그룹에 남은 형제들의 SORT_SEQ를 재번호
		if(!sameGroup) {
			List<Post> oldSiblings = postMapper.selectSiblingPosts(oldParentPostId, oldCategoryId);
			int seq = 0;
			for(Post sibling : oldSiblings) {
				if(sibling.getPostId().equals(postId)) {
					continue;
				}
				postMapper.updatePostSortSeq(sibling.getPostId(), seq++);
			}
		}

		// 새 그룹에 이동한 게시물을 원하는 위치로 끼워넣고 전체 재번호
		List<Post> newSiblings = postMapper.selectSiblingPosts(newParentPostId, newCategoryId);
		List<Long> orderedIds = new ArrayList<Long>();
		for(Post sibling : newSiblings) {
			if(!sibling.getPostId().equals(postId)) {
				orderedIds.add(sibling.getPostId());
			}
		}
		int insertAt = Math.max(0, Math.min(post.getSortSeq(), orderedIds.size()));
		orderedIds.add(insertAt, postId);

		for(int i = 0; i < orderedIds.size(); i++) {
			if(orderedIds.get(i).equals(postId)) {
				continue; // 이동한 게시물 자신은 부모 정보와 함께 아래에서 반영
			}
			postMapper.updatePostSortSeq(orderedIds.get(i), i);
		}

		// 이동한 게시물 자신은 부모 정보 + 최종 순서를 한 번에 반영
		post.setPostId(postId);
		post.setSortSeq(insertAt);
		return postMapper.relocatePost(post);
	}

	/**
	 * 카테고리 직속 최상위 게시물 목록 조회 (부모 게시물 없이 카테고리에 바로 속한 게시물)
	 * @param categoryId
	 * @return
	 */
	public List<Post> selectRootPostsByCategory(Long categoryId) {
		return postMapper.selectSiblingPosts(null, categoryId);
	}

	/**
	 * 게시물 삭제 (하위 게시물이 있으면 함께 삭제)
	 * @param postId
	 * @return
	 */
	public int deletePost(Long postId) {
		List<Post> childPosts = postMapper.selectSiblingPosts(postId, null);
		for(Post childPost : childPosts) {
			this.deletePost(childPost.getPostId());
		}
		return postMapper.deletePost(postId);
	}
}