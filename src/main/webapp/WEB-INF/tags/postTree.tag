<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="post" type="kmjblog.domain.Post" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags" %>

<li class="post-item" id="post_${post.postId}">

    <!-- <c:url var="postUrl" value="/blog/${categoryId}/${post.postId}" />
    <a href="${postUrl}">
        <c:out value="${post.title}" />
    </a> --> 
    
    <c:out value="${post.title}" />
    
    <c:if test="${not empty post.children}">
        <ul class="post-children">
            <c:forEach var="child" items="${post.children}">
                <app:postTree post="${child}" />
            </c:forEach>
        </ul>
    </c:if>

</li>