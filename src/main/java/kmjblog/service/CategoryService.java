package kmjblog.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	public CategoryService(CategoryMapper categoryMapper, PostService postService) {
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
	 * 카테고리 이동 (부모/순서가 바뀌면 형제 카테고리들의 SORT_SEQ도 함께 재정렬)
	 * @return
	 */
	public int relocateCategory(Long categoryId, Category category) {

		// 이동 전 상태 조회 (이전 부모를 알아야 재정렬 대상이 정해짐)
		Category current = categoryMapper.selectCategory(categoryId);
		if(current == null) {
			return 0;
		}

		Long oldParentCategoryId = current.getParentCategoryId();
		Long newParentCategoryId = category.getParentCategoryId();
		boolean sameParent = Objects.equals(oldParentCategoryId, newParentCategoryId);

		// 부모가 바뀌었다면 이전 부모에 남은 형제들의 SORT_SEQ를 재번호
		if(!sameParent) {
			List<Category> oldSiblings = categoryMapper.selectSiblingCategories(oldParentCategoryId);
			int seq = 0;
			for(Category sibling : oldSiblings) {
				if(sibling.getCategoryId().equals(categoryId)) {
					continue;
				}
				categoryMapper.updateCategorySortSeq(sibling.getCategoryId(), seq++);
			}
		}

		// 새 부모에 이동한 카테고리를 원하는 위치로 끼워넣고 전체 재번호
		List<Category> newSiblings = categoryMapper.selectSiblingCategories(newParentCategoryId);
		List<Long> orderedIds = new ArrayList<Long>();
		for(Category sibling : newSiblings) {
			if(!sibling.getCategoryId().equals(categoryId)) {
				orderedIds.add(sibling.getCategoryId());
			}
		}
		int insertAt = Math.max(0, Math.min(category.getSortSeq(), orderedIds.size()));
		orderedIds.add(insertAt, categoryId);

		for(int i = 0; i < orderedIds.size(); i++) {
			if(orderedIds.get(i).equals(categoryId)) {
				continue; // 이동한 카테고리 자신은 부모 정보와 함께 아래에서 반영
			}
			categoryMapper.updateCategorySortSeq(orderedIds.get(i), i);
		}

		// 이동한 카테고리 자신은 부모 정보 + 최종 순서를 한 번에 반영
		category.setCategoryId(categoryId);
		category.setSortSeq(insertAt);
		return categoryMapper.relocateCategory(category);
	}
	
	/**
	 * 카테고리 삭제 (하위 카테고리와 소속 게시물이 있으면 함께 삭제)
	 * @return
	 */
	public int deleteCategory(Long categoryId) {
		List<Category> childCategories = categoryMapper.selectSiblingCategories(categoryId);
		for(Category childCategory : childCategories) {
			this.deleteCategory(childCategory.getCategoryId());
		}

		List<Post> rootPosts = postService.selectRootPostsByCategory(categoryId);
		for(Post rootPost : rootPosts) {
			postService.deletePost(rootPost.getPostId());
		}

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
