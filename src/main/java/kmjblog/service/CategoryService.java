package kmjblog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kmjblog.domain.Category;
import kmjblog.mapper.CategoryMapper;

@Service
public class CategoryService {
	
	private final CategoryMapper categoryMapper;
	
	// 의존성 주입
	private CategoryService(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}
	
	public List<Category> buildCategoryTree() {
		List<Category> categories = categoryMapper.selectCategoryList();
		List<Category> rootCategories = new ArrayList<>();
		
		// 1) 모든 카테고리를 카테고리아이디: 카테고리객체 형태로 Map자료구조의 객체에 저장
		Map<Long, Category> categoryMap = new HashMap<Long, Category>();
		for(Category category : categories) {
			categoryMap.put(category.getCategoryId(), category);
		}
		
		// 2) 모든 카테고리를 순회하면서 부모카테고리에 해당 카테고리를 저장
		for(Category category : categories) {
			
			System.out.println(category.toString());
			
			// 카테고리 아이디, 부모 카테고리 아이디 가져오기
			Long categoryId = category.getCategoryId();
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
}
