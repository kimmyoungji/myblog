package kmjblog.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kmjblog.mapper.CategoryMapper;

@Service
public class CategoryService {
	
	private final CategoryMapper categoryMapper;
	
	// 의존성 주입
	private CategoryService(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}
	
	public List<Map<String, Object>> selectCategoryList() {
		return categoryMapper.selectCategoryList();
	}
}
