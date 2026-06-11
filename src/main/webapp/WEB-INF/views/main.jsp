<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<section class="layout__main">
	<div class="post-meta">
		<p>
			<strong>제목:</strong> posts.title
		</p>
		<p>
			<strong>최종작성일:</strong> 2026년 05월 13일 (수) 오후 10:32
		</p>
		<p>
			<strong>작성자:</strong> posts.author_id.user_email
		</p>
		<p>
			<strong>조회수:</strong> 0
		</p>
	</div>

	<article class="post-box">
		<h2># Hello My blog</h2>
		<h3>## Index</h3>
		<ol>
			<li>history</li>
			<li>project</li>
			<li>study</li>
		</ol>
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
