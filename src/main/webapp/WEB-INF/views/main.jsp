<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<section class="layout__main">
	<div class="post-meta">
		<p>
			<label for="post-title">제목:</label> 
			<input id="post-title" value="${frstPost.title}" disabled/>
		</p>
		<p>
			<label for="post-updatedAt">최종작성일:</label>
			<input id="post-updatedAt" value="${frstPost.updatedAt}" disabled/>
		</p>
		<p>
			<label for="post-authorId">작성자:</label> 
			<input id="post-authorId" value="${frstPost.authorId}" disabled/>
		</p>
		<p>
			<label for="post-viewCount">조회수:</label>
			<input id="post-viewCount" value="${frstPost.viewCount}" disabled/>
		</p>
	</div>

	<article>
		<textarea id="post-content"
				  rows="6"
				  cols="22"
				  minlength="10"
				  maxlength="20"
				  disabled
				  placeholder="띵거가 말하지 못한 것들...">${frstPost.content}</textarea>
	</article>

	<section class="comments">
		<h3>댓글</h3>

		<div class="comment-card">
			<strong>작성자: comments.author_id.user_email</strong>
			<p>안녕하세요</p>
			<span>작성일시: 2026.05.13 00:33</span>
			<button type="button">답글쓰기</button>
		</div>

		<div class="comment-card reply">
			<strong>작성자: comments.author_id.user_email</strong>
			<p>안녕하세요~</p>
			<span>작성일시: 2026.05.13 00:33</span>
			<button type="button">답글쓰기</button>
		</div>

		<div class="comment-form">
			<textarea placeholder="댓글을 남겨보세요"></textarea>
			<div class="form-actions">
				<button type="button" class="btn btn-text">취소</button>
				<button type="button" class="btn btn-primary">등록</button>
			</div>
		</div>
	</section>
</section>
