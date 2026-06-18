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
	private int sortOrder;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<Category> children = new ArrayList<Category>();
	
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

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
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
	
	@Override
	public String toString() {
		return "Category{" +
				"categoryId=" + categoryId +
				", parentCategoryId=" + parentCategoryId +
				", name='" + name + '\'' +
				", sortOrder=" + sortOrder +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", children=" + children +
				'}';
	}
}
