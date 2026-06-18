<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags" %>    

<!DOCTYPE html>
<aside class="layout__nav">
    <nav class="category-nav" aria-label="블로그 카테고리">
        <ul class="category-tree">
            <c:forEach var="category" items="${categoryTree}">
                <app:categoryTree category="${category}" />
            </c:forEach>
        </ul>
    </nav>
</aside>