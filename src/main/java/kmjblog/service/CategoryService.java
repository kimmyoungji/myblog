package kmjblog.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.stereotype.Service;

import kmjblog.domain.Category;
import kmjblog.domain.Post;
import kmjblog.mapper.CategoryMapper;

@Service
public class CategoryService {
	
	private final CategoryMapper categoryMapper;
	private final PostService postService;
	
	// 의존성 주입
	private CategoryService(CategoryMapper categoryMapper, PostService postService) {
		this.categoryMapper = categoryMapper;
		this.postService = postService;
	}
	
	/**
	 * 카테고리 트리 조회
	 * @return
	 */
	public List<Category> selectCategoryList() {
		return categoryMapper.selectCategoryList();
	}
	
	/**
	 * 카테고리 트리 조회
	 * @return
	 */
	public List<Category> buildCategoryTree() {
		List<Category> categories = categoryMapper.selectCategoryList();
		return this.convertCategoryListToTree(categories);
	}
	
	/**
	 * 카테고리 추가 (categoryId는 DB가 채번)
	 * @return
	 */
	public int insertCategory(Category category) {
		return categoryMapper.insertCategory(category);
	}

	/**
	 * 카테고리 수정
	 * @return
	 */
	public int updateCategory(Long categoryId, Category category) {
		category.setCategoryId(categoryId);
		return categoryMapper.updateCategory(category);
	}

	/**
	 * 카테고리 이름만 변경
	 * @return
	 */
	public int renameCategory(Long categoryId, String name) {
		Category category = new Category();
		category.setCategoryId(categoryId);
		category.setName(name);
		return categoryMapper.renameCategory(category);
	}
	
	/**
	 * 카테고리 삭제
	 * @return
	 */
	public int deleteCategory(Long categoryId) {
		return categoryMapper.deleteCategory(categoryId);
	}
	
	
	
	/**
	 * 게시물을 포함한 카테고리 트리 조회
	 * @return
	 */
	public List<Category> buildCategoryTreeWithPosts() {
		
		// 카테고리 목록
		List<Category> categories = categoryMapper.selectCategoryList();
		
		// 루트 게시물 목록 조회
		List<Post> rootPosts = postService.buildPostTree();
		
		// 카테고리 아이디를 기준으로 카테고리 목록에 루트 게시물을 할당
		for(Post post: rootPosts) {
			Long postCategoryId = post.getCategoryId();
			for(Category category: categories) {
				if(category.getCategoryId() == postCategoryId) {
					category.getPosts().add(post);
				}
			}
		}
		
		// 카테고리 목록을 트리 형태로 변환해서 반환
		return this.convertCategoryListToTree(categories);
	}
	
	/**
	 * List<Category> 를 트리형태의 데이터로 변환 
	 * @param categories
	 * @return
	 */
	public List<Category> convertCategoryListToTree(List<Category> categories) {
		// 반환 객체 준비
		List<Category> rootCategories = new ArrayList<>();
		
		// 빈 카테고리 배열 예외처리
		if(categories == null && categories.isEmpty()) {
			return rootCategories;
		}
		
		// 1) 모든 카테고리를 카테고리아이디: 카테고리객체 형태로 Map자료구조의 객체에 저장
		Map<Long, Category> categoryMap = new HashMap<Long, Category>();
		for(Category category : categories) {
			categoryMap.put(category.getCategoryId(), category);
		}
		
		// 2) 모든 카테고리를 순회하면서 부모카테고리에 해당 카테고리를 저장
		for(Category category : categories) {
			
			// System.out.println(category.toString());
			
			// 카테고리 아이디, 부모 카테고리 아이디 가져오기
			//Long categoryId = category.getCategoryId();
			Long parentCatetoryId = category.getParentCategoryId();
			
			// 루트 카테고리 일경우
			if(parentCatetoryId == null) {
				rootCategories.add(category);
				continue;
			}
			
			// 루트 카테고리가 아닐 경우: 부모 카테고리에 자식으로 넣어주기
			Category parentCategory = categoryMap.get(parentCatetoryId);
			if(parentCategory != null) {
				List<Category> children = parentCategory.getChildren();
				children.add(category);
			}
		}
		
		return rootCategories;
	}
	
	/**
	 * 카테고리 트리 데이터에서 게시글이 있는 첫번째 카테고리 찾기
	 * @param categoryTree
	 * @return
	 */
	public Category findFrstCatHasPost(List<Category> categoryTree) {
		for(Category category: categoryTree) {
			if(!category.getPosts().isEmpty()) {
				return category; 
			}
			
			List<Category> children = category.getChildren();
			return this.findFrstCatHasPost(children);
		}
		return null;
	}
	
}
