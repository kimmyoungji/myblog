// 블로그 화면의 진입점. 각 모듈은 window.XXX = { init, ... } 형태로 정의만 해두고
// 실제 DOM 바인딩/이벤트 등록은 여기서 순서대로 호출한 뒤에 실행된다.
// CategoryTree는 트리 클릭 시 PostPanel/PostList를 참조하므로 항상 마지막에 초기화한다.
// Auth는 CategoryTree가 관리자 여부(컨텍스트 메뉴/드래그앤드롭 노출)를 판단하는 데
// 쓰이므로, 로그인 상태 조회가 끝날 때까지 기다린 뒤 나머지를 초기화한다.
document.addEventListener("DOMContentLoaded", async function () {
  await window.Auth.init();
  window.CategoryTree.init();
  window.PostList.init();
  window.PostPanel.init();
});
