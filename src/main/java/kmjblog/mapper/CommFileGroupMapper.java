package kmjblog.mapper;

import org.apache.ibatis.annotations.Mapper;

import kmjblog.domain.CommFileGroupVO;

@Mapper
public interface CommFileGroupMapper {
	public CommFileGroupVO selectFileGroupByPostId(Long postId);
	public int insertFileGroup(CommFileGroupVO commFileGroupVo);
	public int deleteFileGroupByPostId(Long postId);
}
