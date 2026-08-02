<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<section class="layout__detail">
	<div id="post-panel" class="post-panel"
		 data-mode="${empty frstPost ? 'initial' : 'view'}"
		 data-post-id="<c:out value='${frstPost.postId}'/>">

		<p class="post-empty">왼쪽 트리에서 게시글을 선택해주세요.</p>

		<div class="post-meta">
			<input id="post-title" class="post-title" value="<c:out value='${frstPost.title}'/>" disabled/>
			<p class="post-meta-line">
				작성자: <span id="post-authorId"><c:out value='${frstPost.authorId}'/></span>
				/ 최종작성일: <span id="post-updatedAt"><c:out value='${frstPost.updatedAt}'/></span>
				/ 조회수: <span id="post-viewCount"><c:out value='${frstPost.viewCount}'/></span>
				/ xxs-test: <span id="post-viewCount"><c:out value='${frstPost.title}'/></span>
			</p>
		</div>

		<article class="post-box">
			<!-- Vditor의 실제 데이터 저장소. 화면에는 표시하지 않고 post-editor.js가 값을 읽고 쓰는 용도로만 쓴다. -->
			<textarea id="post-content"
					  class="is-hidden"
					  disabled><c:out value='${frstPost.content}'/></textarea>
			<!-- Vditor가 생성될 영역 -->
    		<div id="post-vditor"></div>
			<!-- 조회 모드에서 마크다운을 렌더링해서 보여주는 영역 -->
			<div id="post-preview"></div>
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
