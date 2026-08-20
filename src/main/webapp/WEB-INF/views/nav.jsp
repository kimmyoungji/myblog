<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="app"
tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<div id="nav-backdrop" class="nav-backdrop"></div>
<aside class="layout__nav">
  <nav class="category-nav" aria-label="블로그 카테고리">
    <%--
    <ul class="category-tree">
      <c:forEach var="category" items="${categoryTreeWithPosts}">
        <app:categoryTree category="${category}" />
      </c:forEach>
    </ul>
    --%>
    <div id="category-jstree"></div>
  </nav>
</aside>
