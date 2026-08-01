<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<section class="layout__detail">
	<div id="post-panel" class="post-panel"
		 data-mode="${empty frstPost ? 'initial' : 'view'}"
		 data-post-id="${frstPost.postId}">

		<p class="post-empty">왼쪽 트리에서 게시글을 선택해주세요.</p>

		<div class="post-meta">
			<input id="post-title" class="post-title" value="${frstPost.title}" disabled/>
			<p class="post-meta-line">
				작성자: <span id="post-authorId">${frstPost.authorId}</span>
				/ 최종작성일: <span id="post-updatedAt">${frstPost.updatedAt}</span>
				/ 조회수: <span id="post-viewCount">${frstPost.viewCount}</span>
			</p>
		</div>

		<article class="post-box">
			<textarea id="post-content"
					  disabled
					  placeholder="띵거가 말하지 못한 것들...">${frstPost.content}</textarea>
			<!-- Vditor가 생성될 영역 -->
    		<div id="post-vditor"></div>

			<div class="post-toolbar">
				<button type="button" class="btn btn-text" data-action="delete">삭제</button>
				<button type="button" class="btn btn-text" data-action="cancel">취소</button>
				<button type="button" class="btn btn-primary" data-action="edit">수정</button>
				<button type="button" class="btn btn-primary" data-action="save">저장</button>
			</div>
		</article>
	</div>

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
