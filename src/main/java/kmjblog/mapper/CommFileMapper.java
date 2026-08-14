package kmjblog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kmjblog.domain.CommFileVO;

@Mapper
public interface CommFileMapper {
	public int insertFile(CommFileVO commFileVo);
	public List<CommFileVO> selectFilesByFileGroupId(Long fileGroupId);
	public int deleteFilesByIds(List<Long> fileIds);
	public int deleteFileByFileGrpId(Long fileGroupId);
}
