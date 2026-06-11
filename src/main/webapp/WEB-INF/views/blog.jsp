<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>dinguru</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/typography.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/blog.css" />
</head>
<body>
  <jsp:include page="header.jsp"></jsp:include>
  <main class="layout">
  	<jsp:include page="nav.jsp"></jsp:include>
    <jsp:include page="main.jsp"></jsp:include>
  </main>
  <jsp:include page="footer.jsp"></jsp:include>
</body>
</html>