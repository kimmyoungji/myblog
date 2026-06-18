package kmjblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kmjblog.domain.Category;

@Mapper
public interface CategoryMapper {
	/* 카테고리 목록조회*/
	public List<Category> selectCategoryList();
}
