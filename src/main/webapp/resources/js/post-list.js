window.PostList = (function () {
  let listSection, table;
  // 마지막으로 조회한 카테고리. 일괄삭제 후 목록을 다시 불러올 때 사용한다.
  let currentCategoryId = null;

  function init() {
    listSection = document.querySelector(".layout__list");
    const postListEl = document.querySelector("#post-list");
    const btnNewPost = document.querySelector("#btn-new-post");
    const btnBulkDelete = document.querySelector("#btn-bulk-delete");

    // Tabulator 테이블 생성
    table = new Tabulator(postListEl, {
      layout: "fitColumns",
      placeholder: "게시글이 없습니다.",
      columns: [
        {
          formatter: "rowSelection",
          titleFormatter: "rowSelection",
          hozAlign: "center",
          headerSort: false,
          width: 40,
        },
        { title: "ID", field: "postId", visible: false },
        { title: "제목", field: "title" },
        { title: "작성자", field: "authorId" },
        { title: "순번", field: "sortSeq", visible: false },
        { title: "조회수", field: "viewCount" },
        { title: "최종작성일", field: "updatedAt" },
      ]
    });

    table.on("rowClick", async (e, row) => {
      // 체크박스 클릭은 선택만 토글하고, 게시글 상세로 진입하지 않는다.
      if (e.target.tagName === "INPUT") return;

      const postId = row.getData().postId;

      // 이전 게시글이 화면에 노출된 채로 있다가 새 데이터로 바뀌며 깜빡이는 것을
      // 막기 위해, 상세 데이터 로드가 끝난 뒤에 목록을 숨기고 패널을 보여준다.
      try {
        await window.PostPanel.load(postId);
      } catch (error) {
        // 로드 실패 시 목록 화면에 그대로 머무른다.
        return;
      }
      window.PostList.hide();
      window.PostPanel.show();
    });

    // 새 글 작성 버튼 클릭 시
    btnNewPost.addEventListener("click", () => {
      window.PostPanel.createNewPost();
    });

    // 선택 삭제 버튼 클릭 시
    btnBulkDelete.addEventListener("click", bulkDelete);
  }

  // 카테고리에 속한 게시글 목록을 불러온다 (category-tree.js에서 카테고리 선택 시 호출)
  async function load(categoryId) {
    currentCategoryId = categoryId;
    try {
      const posts = await window.Api.get(`/blog/api/post?categoryId=${categoryId}`);
      table.setData(posts ?? []);
    } catch (error) {
      console.error(error);
      alert("게시글 목록을 불러오는 중 오류가 발생했습니다: " + error.message);
    }
  }

  // 체크된 게시글을 일괄삭제한다 (하위 게시물이 있으면 서버에서 함께 삭제된다).
  async function bulkDelete() {
    const selectedIds = table.getSelectedData().map((row) => row.postId);

    if (selectedIds.length === 0) {
      alert("삭제할 게시글을 선택해주세요.");
      return;
    }
    if (!confirm(`선택한 게시글 ${selectedIds.length}건을 삭제하시겠습니까?`)) return;

    try {
      await window.Api.delete("/blog/api/post", selectedIds);

      // 현재 패널에 열려있는 게시글이 삭제 대상에 포함되면 패널을 초기화한다.
      const panel = document.querySelector("#post-panel");
      if (panel && selectedIds.some((id) => String(id) === panel.dataset.postId)) {
        window.PostPanel.reset();
        window.PostPanel.hide();
        window.PostList.show();
      }

      // 목록/트리를 최신 상태로 다시 불러온다.
      await Promise.all([load(currentCategoryId), window.CategoryTree.reload()]);
    } catch (error) {
      console.error(error);
      alert("게시글 일괄삭제 중 오류가 발생했습니다: " + error.message);
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
