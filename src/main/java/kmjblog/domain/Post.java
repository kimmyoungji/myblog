package kmjblog.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시물 클래스
 */
public class Post {
	
	/* members */
	private Long postId;
	private Long categoryId;
	private Long parentPostId;
	private Long authorId;
	private Long fileGroupId;
	private Integer sortSeq;
	private String title;
	private String content;
	private int  viewCount;
	private char delYn;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<Post> children = new ArrayList<Post>();
	
	/* 생성자 */
	public Post () {}

	/* Getters & Setters */
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getParentPostId() {
		return parentPostId;
	}

	public void setParentPostId(Long parentPostId) {
		this.parentPostId = parentPostId;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public Long getFileGroupId() {
		return fileGroupId;
	}

	public void setFileGroupId(Long fileGroupId) {
		this.fileGroupId = fileGroupId;
	}

	public Integer getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(Integer sortSeq) {
		this.sortSeq = sortSeq;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public char getDelYn() {
		return delYn;
	}

	public void setDelYn(char delYn) {
		this.delYn = delYn;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<Post> getChildren() {
		return children;
	}

	public void setChildren(List<Post> children) {
		this.children = children;
	}

	
	@Override
	public String toString() {
		return "Post [postId=" + postId + ", categoryId=" + categoryId + ", parentPostId=" + parentPostId
				+ ", authorId=" + authorId + ", fileGroupId=" + fileGroupId + ", sortSeq=" + sortSeq + ", title="
				+ title + ", content=" + content + ", viewCount=" + viewCount + ", delYn=" + delYn + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", children=" + children + "]";
	}
}