<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<section class="layout__list is-hidden">
  <div class="list__header">
    <h2>게시글 목록</h2>
    <div class="btn-box">
      <button
        type="button"
        class="btn btn-danger"
        id="btn-bulk-delete"
        data-admin-only
      >
        선택 삭제
      </button>
      <button
        type="button"
        class="btn btn-primary"
        id="btn-new-post"
        data-admin-only
      >
        새 글 작성
      </button>
    </div>
  </div>

  <div id="post-list" class="post-list"></div>
</section>
