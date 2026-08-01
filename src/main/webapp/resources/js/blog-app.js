// 블로그 화면의 진입점. 각 모듈은 window.XXX = { init, ... } 형태로 정의만 해두고
// 실제 DOM 바인딩/이벤트 등록은 여기서 순서대로 호출한 뒤에 실행된다.
// CategoryTree는 트리 클릭 시 PostPanel/PostList를 참조하므로 항상 마지막에 초기화한다.
document.addEventListener("DOMContentLoaded", function () {
  window.CategoryTree.init();
  window.PostList.init();
  window.PostPanel.init();
});
