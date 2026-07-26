package kmjblog.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 카테고리 클래스
 */
public class Category {
	
	/* members */
	private Long categoryId;
	private Long parentCategoryId;
	private String name;
	private int sortSeq;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<Category> children = new ArrayList<Category>();
	private List<Post> posts = new ArrayList<Post>();

	/* 생성자 */
	public Category () {}
	
	/* Getters & Setters */
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSortSeq() {
		return sortSeq;
	}

	public void setSortSeq(int sortSeq) {
		this.sortSeq = sortSeq;
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

	public List<Category> getChildren() {
		return children;
	}

	public void setChildren(List<Category> children) {
		this.children = children;
	}
	
	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", parentCategoryId=" + parentCategoryId + ", name=" + name
				+ ", sortSeq=" + sortSeq + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", children="
				+ children + ", posts=" + posts + "]";
	}
	
}
