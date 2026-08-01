window.PostList = (function () {
  let listSection, table;

  function init() {
    listSection = document.querySelector(".layout__list");
    const postListEl = document.querySelector("#post-list");
    const btnNewPost = document.querySelector("#btn-new-post");

    // Tabulator 테이블 생성
    table = new Tabulator(postListEl, {
      layout: "fitColumns",
      placeholder: "게시글이 없습니다.",
      columns: [
        { title: "ID", field: "postId", visible: false },
        { title: "제목", field: "title" },
        { title: "작성자", field: "authorId" },
        { title: "순번", field: "sortSeq", visible: false },
        { title: "조회수", field: "viewCount" },
        { title: "최종작성일", field: "updatedAt" },
      ]
    });

    table.on("rowClick", (e, row) => {
      console.log("rowClick", row.getData());
      window.PostList.hide();
      window.PostPanel.show();
      const postId = row.getData().postId;
      window.PostPanel.load(postId);
    });

    // 새 글 작성 버튼 클릭 시
    btnNewPost.addEventListener("click", () => {
      window.PostPanel.createNewPost();
    });
  }

  // 카테고리에 속한 게시글 목록을 불러온다 (category-tree.js에서 카테고리 선택 시 호출)
  async function load(categoryId) {
    try {
      const response = await fetch(`/blog/api/post?categoryId=${categoryId}`);
      const result = await response.json();

      if (!response.ok || !result.success) {
        throw new Error(result.message || "게시글 목록 조회 요청 실패");
      }

      table.setData(result.data ?? []);
    } catch (error) {
      console.error(error);
      alert("게시글 목록을 불러오는 중 오류가 발생했습니다: " + error.message);
    }
  }

  function show() {
    listSection.classList.remove("is-hidden");
    table.redraw(true);
  }

  function hide() {
    listSection.classList.add("is-hidden");
  }

  // blog-app.js가 init()을 호출해 테이블을 만든 뒤, category-tree.js가 load/show/hide를 호출한다.
  return { init, load, show, hide };
})();
