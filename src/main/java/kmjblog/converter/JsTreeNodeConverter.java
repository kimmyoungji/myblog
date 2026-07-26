package kmjblog.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import kmjblog.domain.Category;
import kmjblog.domain.JsTreeNodeDto;
import kmjblog.domain.Post;

@Component
public class JsTreeNodeConverter {
	
	/**
	 * Category 형태의 데이터를 JsTreeNodeDto 형태로 변환하기
	 * @param category
	 * @return
	 */
	public JsTreeNodeDto fromCategory(Category category) {
		
		String parentId = category.getParentCategoryId() != null
	            ? "category_" + category.getParentCategoryId()
	            : "#";
		
		Map<String, Object> data = new HashMap<>();
		data.put("categoryId", category.getCategoryId());
		data.put("parentCategoryId", category.getParentCategoryId());
		
		return new JsTreeNodeDto(
				"category_" + category.getCategoryId(),
				parentId,
				category.getName(),
				"category",
				data
		);
	}
	
	/**
	 * List<Category> 형태의 데이터를 List<JsTreeNodeDto> 형태로 변환하기
	 * @param category
	 * @return
	 */
	public List<JsTreeNodeDto> fromCategories(List<Category> categories) {
		
		List<JsTreeNodeDto> jsTreeNodes = new ArrayList<>();
		
		for(Category cat : categories) {
			jsTreeNodes.add(this.fromCategory(cat));
		}
		
		return jsTreeNodes;
	}
	
	/**
	 * Post 데이터를 JsTreeNodeDto 형태로 변환
	 * @param post
	 * @return
	 */
	public JsTreeNodeDto fromPost(Post post) {
		
		String parentId = post.getParentPostId() != null
	            ? "post_" + post.getParentPostId()
	            : "category_" + post.getCategoryId();
		
		Map<String, Object> data = new HashMap<>();
		data.put("postId", post.getPostId());
		data.put("categoryId", post.getCategoryId());
		data.put("parentPostId", post.getParentPostId());

	    return new JsTreeNodeDto(
	            "post_" + post.getPostId(),
	            parentId,
	            post.getTitle(),
	            "post",
	            data
	    );
	}
	
	/**
	 * List<Category> 형태의 데이터를 List<JsTreeNodeDto> 형태로 변환하기
	 * @param category
	 * @return
	 */
	public List<JsTreeNodeDto> fromPosts(List<Post> posts) {
		
		List<JsTreeNodeDto> jsTreeNodes = new ArrayList<>();
		
		for(Post post : posts) {
			jsTreeNodes.add(this.fromPost(post));
		}
		
		return jsTreeNodes;
	}
}
