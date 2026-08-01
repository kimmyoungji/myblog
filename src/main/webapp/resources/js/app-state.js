// 화면 전역에서 여러 모듈이 함께 참조하는 값(현재 선택된 categoryId/postId 등)을
// 한곳에서 관리한다. 각 모듈은 여기서 값을 읽고 쓸 뿐, 직접 들고 있지 않는다.
window.AppState = (function () {
  let categoryId = null;
  let postId = null;

  return {
    getCategoryId: () => categoryId,
    setCategoryId: (id) => {
      categoryId = id;
    },
    getPostId: () => postId,
    setPostId: (id) => {
      postId = id;
    },
  };
})();