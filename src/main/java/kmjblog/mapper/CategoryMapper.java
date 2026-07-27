package kmjblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kmjblog.domain.Category;

@Mapper
public interface CategoryMapper {
	public List<Category> selectCategoryList();
	public Category selectCategory(Long categoryId);
	public int insertCategory(Category category);
	public int updateCategory(Category category);
	public int renameCategory(Category category);
	public int relocateCategory(Category category);
	public List<Category> selectSiblingCategories(Long parentCategoryId);
	public int updateCategorySortSeq(@Param("categoryId") Long categoryId, @Param("sortSeq") int sortSeq);
	public int deleteCategory(Long categoryId);
}
