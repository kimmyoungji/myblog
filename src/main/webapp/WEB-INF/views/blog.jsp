<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>dinguru</title>
    <jsp:include page="loadFile.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="header.jsp"></jsp:include>
    <main class="layout">
      <jsp:include page="nav.jsp"></jsp:include>
      <jsp:include page="detail.jsp"></jsp:include>
      <jsp:include page="list.jsp"></jsp:include>
    </main>
    <jsp:include page="footer.jsp"></jsp:include>
  </body>
</html>
