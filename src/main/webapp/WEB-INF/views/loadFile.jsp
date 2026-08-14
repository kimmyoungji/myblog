<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>

<!-- jQuery SCRIPT -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"></script>

<!-- jsTree CSS -->
<link
  rel="stylesheet"
  href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css"
/>

<!-- jsTree SCRIPT -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>

<!-- Font Awesome CSS -->
<link
  rel="stylesheet"
  href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
/>

<!-- Tabulator CSS -->
<link
  rel="stylesheet"
  href="https://unpkg.com/tabulator-tables@6.5.0/dist/css/tabulator.min.css"
/>

<!-- Tabulator SCRIPT -->
<script src="https://unpkg.com/tabulator-tables@6.5.0/dist/js/tabulator.min.js"></script>

<!-- Vditor CSS -->
<link rel="stylesheet" href="https://unpkg.com/vditor@3.11.2/dist/index.css" />

<!-- Vditor SCRIPT -->
<script src="https://unpkg.com/vditor@3.11.2/dist/index.min.js"></script>

<!-- CUSTOM CSS: 외부 라이브러리 기본 스타일을 덮어써야 하므로 항상 마지막에 로드한다 -->
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/css/reset.css"
/>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/css/variables.css"
/>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/css/layout.css"
/>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/css/typography.css"
/>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/css/component.css"
/>
<link
  rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/css/blog.css"
/>

<!-- CUSTOM SCRIPT: 모듈은 window.XXX = { init, ... } 형태로만 정의되며,
     실제 초기화는 blog-app.js가 순서대로 호출한다. -->
<script src="${pageContext.request.contextPath}/resources/js/app-state.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/api.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/auth.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/post-panel.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/post-list.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/category-tree.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/blog-app.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/post-editor.js"></script>
