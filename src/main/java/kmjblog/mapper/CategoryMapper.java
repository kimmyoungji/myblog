package kmjblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kmjblog.domain.Category;

@Mapper
public interface CategoryMapper {
	public List<Category> selectCategoryList();
	public int insertCategory(Category category);
	public int updateCategory(Category category);
	public int renameCategory(Category category);
	public int deleteCategory(Long categoryId);
}
