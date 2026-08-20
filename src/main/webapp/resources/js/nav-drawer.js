// 모바일 폭(<768px, responsive.css 브레이크포인트와 동일)에서 카테고리 트리(.layout__nav)를
// 오버레이 드로어로 열고 닫는다. 데스크탑 폭에서는 사이드바가 항상 보이므로 동작하지 않는다.
window.NavDrawer = (function () {
  let nav, backdrop, toggle;

  function open() {
    nav.classList.add("is-open");
    backdrop.classList.add("is-open");
  }

  function close() {
    nav.classList.remove("is-open");
    backdrop.classList.remove("is-open");
  }

  function init() {
    nav = document.querySelector(".layout__nav");
    backdrop = document.querySelector("#nav-backdrop");
    toggle = document.querySelector("#nav-toggle");

    // 로그인 화면처럼 카테고리 트리(nav)가 없는 페이지에서는 드로어를 붙이지 않는다.
    if (!nav || !backdrop || !toggle) return;

    toggle.addEventListener("click", () => {
      nav.classList.contains("is-open") ? close() : open();
    });
    backdrop.addEventListener("click", close);
  }

  return { init, open, close };
})();
