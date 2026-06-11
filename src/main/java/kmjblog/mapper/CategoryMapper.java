package kmjblog.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
	/* 카테고리 목록조회*/
	public List<Map<String, Object>> selectCategoryList();
}
