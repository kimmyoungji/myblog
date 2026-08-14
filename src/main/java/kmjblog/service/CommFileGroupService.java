package kmjblog.service;

import org.springframework.stereotype.Service;

import kmjblog.domain.CommFileGroupVO;
import kmjblog.mapper.CommFileGroupMapper;

@Service
public class CommFileGroupService {
	
	private final CommFileGroupMapper commFileGroupMapper;
	
	public CommFileGroupService(CommFileGroupMapper commFileGroupMapper) {
		this.commFileGroupMapper = commFileGroupMapper;
	}
	
	public CommFileGroupVO selectFileGroupByPostId(Long postId) {
		return commFileGroupMapper.selectFileGroupByPostId(postId);
	}
	
	public int insertFileGroup(CommFileGroupVO commFileGroupVo) {
		return commFileGroupMapper.insertFileGroup(commFileGroupVo);
	}

	/**
	 * 게시글의 file_group을 조회하고, 없으면 새로 만들어 반환한다.
	 * @param postId
	 * @return
	 */
	public CommFileGroupVO getOrCreateFileGroup(Long postId) {
		CommFileGroupVO fileGroup = commFileGroupMapper.selectFileGroupByPostId(postId);
		if (fileGroup != null) {
			return fileGroup;
		}

		CommFileGroupVO newFileGroup = new CommFileGroupVO();
		newFileGroup.setPostId(postId);
		commFileGroupMapper.insertFileGroup(newFileGroup);
		return commFileGroupMapper.selectFileGroupByPostId(postId);
	}

	public int deleteFileGroupByPostId(Long postId) {
		return commFileGroupMapper.deleteFileGroupByPostId(postId);
	}
}
