// 로그인 상태를 관리하는 모듈. 페이지 로드 시 서버 세션을 조회해 헤더의
// 로그인/로그아웃 UI를 그리고, 다른 모듈(category-tree.js, post-panel.js 등)이
// window.Auth.isAdmin()으로 관리자 전용 UI를 보여줄지 판단할 수 있게 한다.
window.Auth = (function () {
  let currentUser = null; // { userId, email, roleCodes } 또는 로그아웃 상태면 null

  async function init() {
    bindLoginForm();
    await refreshCurrentUser();
  }

  async function refreshCurrentUser() {
    try {
      currentUser = await window.Api.get("/blog/api/auth/me");
    } catch (error) {
      // 로그인하지 않은 상태(401)도 이 경로를 타는 정상적인 흐름이므로 별도 로깅은 하지 않는다.
      currentUser = null;
    }
    // 글쓰기/수정/삭제 등 [data-admin-only] 요소의 노출 여부는 blog.css의
    // "body:not(.is-admin) [data-admin-only]" 규칙이 이 클래스를 보고 판단한다.
    document.body.classList.toggle("is-admin", isAdmin());
    renderUserMenu();
  }

  function isLoggedIn() {
    return currentUser !== null;
  }

  function isAdmin() {
    return isLoggedIn() && (currentUser.roleCodes || []).includes("ADMIN");
  }

  async function logout() {
    try {
      await window.Api.post("/blog/api/auth/logout");
    } catch (error) {
      console.error(error);
    }
    location.href = "/blog/";
  }

  function renderUserMenu() {
    const el = document.querySelector("#user-menu");
    if (!el) return;

    el.innerHTML = "";

    if (isLoggedIn()) {
      const emailSpan = document.createElement("span");
      emailSpan.textContent = "👤 " + currentUser.email;

      const logoutBtn = document.createElement("button");
      logoutBtn.type = "button";
	  logoutBtn.classList.add("btn");
      logoutBtn.textContent = "로그아웃";
      logoutBtn.addEventListener("click", logout);

      el.append(emailSpan, logoutBtn);
    } else {
      const loginLink = document.createElement("a");
      loginLink.href = "/login";
      loginLink.textContent = "👤 로그인";

      el.append(loginLink);
    }
  }

  function bindLoginForm() {
    const form = document.querySelector("#login-form");
    if (!form) return;

    form.addEventListener("submit", async function (event) {
      event.preventDefault();

      const email = form.querySelector("#login-email").value;
      const password = form.querySelector("#login-password").value;
      const errorEl = form.querySelector("#login-error");
      if (errorEl) errorEl.textContent = "";

      try {
        await window.Api.post("/blog/api/auth/login", { email, password });
        location.href = "/blog/";
      } catch (error) {
        console.error(error);
        if (errorEl) errorEl.textContent = error.message;
      }
    });
  }

  return { init, isLoggedIn, isAdmin, logout };
})();
