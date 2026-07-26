<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="category" type="kmjblog.domain.Category" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags" %>

<li class="category-item">

    <!-- <c:url var="categoryUrl" value="/blog/category/${category.categoryId}" /> 
    <a href="${categoryUrl}">
        <c:out value="${category.name}" />
    </a> -->
    
    <c:out value="${category.name}" />
    
    <c:if test="${not empty category.children or not empty category.posts}">
        <ul class="category-children">
            
            <c:forEach var="post" items="${category.posts}">
                <app:postTree post="${post}" />
            </c:forEach> 
            
            <c:forEach var="child" items="${category.children}">
                <app:categoryTree category="${child}" />
            </c:forEach> 
            
        </ul>
    </c:if>

</li>