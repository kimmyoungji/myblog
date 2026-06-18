<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="category" type="kmjblog.domain.Category" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags" %>

<li class="category-item">

    <c:url var="categoryUrl" value="/blog/category/${category.categoryId}" />
    
    <a href="${categoryUrl}">
        <c:out value="${category.name}" />
    </a>
    
    <c:if test="${not empty category.children}">
        <ul class="category-children">
            <c:forEach var="child" items="${category.children}">
                <app:categoryTree category="${child}" />
            </c:forEach>
        </ul>
    </c:if>

</li>