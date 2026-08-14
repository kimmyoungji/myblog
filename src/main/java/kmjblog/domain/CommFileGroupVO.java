package kmjblog.domain;

import org.springframework.web.multipart.MultipartFile;

public class CommFileGroupVO {
	Long fileGroupId;
	Long postId;
	public Long getFileGroupId() {
		return fileGroupId;
	}
	public void setFileGroupId(Long fileGroupId) {
		this.fileGroupId = fileGroupId;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	
}
