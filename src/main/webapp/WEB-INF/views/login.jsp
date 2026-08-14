<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>로그인 - dinguru</title>
    <jsp:include page="loadFile.jsp"></jsp:include>
  </head>

  <body class="page-login">
    <jsp:include page="header.jsp"></jsp:include>
    <main class="layout">
      <section class="login-page">
        <div class="login-card">
          <h2 class="login-title">로그인</h2>
          <form id="login-form" class="login-form">
            <div class="form-field">
              <label for="login-email">이메일</label>
              <input type="email" id="login-email" name="email" autocomplete="username" required />
            </div>
            <div class="form-field">
              <label for="login-password">비밀번호</label>
              <input type="password" id="login-password" name="password" autocomplete="current-password" required />
            </div>
            <p id="login-error" class="login-error"></p>
            <button type="submit" class="btn btn-primary login-submit">로그인</button>
          </form>
        </div>
      </section>
    </main>
    <jsp:include page="footer.jsp"></jsp:include>
  </body>
</html>
