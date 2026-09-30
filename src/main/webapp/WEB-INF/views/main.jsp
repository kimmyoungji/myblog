<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<section class="layout__detail">
	<div id="post-panel" class="post-panel"
		 data-mode="view"
		 data-post-id="">

		<p class="post-empty">왼쪽 트리에서 게시글을 선택해주세요.</p>

		<div class="post-meta">
			<input id="post-title" class="post-title" value="" disabled/>
			<p class="post-meta-line">
				작성자: <span id="post-authorId"></span>
				/ 최종작성일: <span id="post-updatedAt"></span>
				/ 조회수: <span id="post-viewCount"></span>
			</p>
		</div>

		<article class="post-box">
			<textarea id="post-content"
					  disabled
					  placeholder="띵거가 말하지 못한 것들..."></textarea>

			<div class="post-toolbar">
				<button type="button" class="btn btn-text" data-action="delete">삭제</button>
				<button type="button" class="btn btn-text" data-action="cancel">취소</button>
				<button type="button" class="btn btn-primary" data-action="edit">수정</button>
				<button type="button" class="btn btn-primary" data-action="save">저장</button>
			</div>
		</article>
	</div>
</section>
