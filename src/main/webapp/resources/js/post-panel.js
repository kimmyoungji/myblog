// 게시글 영역(#post-panel)의 상태. detail.jsp의 data-mode 초기값과 문자열이 일치해야 한다.
const PostPanelMode = Object.freeze({
  INITIAL: "initial",
  VIEW: "view",
  EDIT: "edit",
});

window.PostPanel = (function () {
  let panel, detailSection, els, postId, snapshot;
  // "새 글 작성"으로 막 만들어져 아직 한 번도 저장된 적 없는 게시글인지 여부.
  // 이 상태에서 취소하면(빈 글이 DB에 남더라도) 빈 조회 화면을 보여주지 않고
  // 목록으로 돌아간다.
  let isUnsavedNewPost = false;

  function init() {
    panel = document.querySelector("#post-panel");
    if (!panel) return;

    detailSection = document.querySelector(".layout__detail");

    els = {
      title: panel.querySelector("#post-title"),
      updatedAt: panel.querySelector("#post-updatedAt"),
      authorId: panel.querySelector("#post-authorId"),
      viewCount: panel.querySelector("#post-viewCount"),
      content: panel.querySelector("#post-content"),
    };

    postId = panel.dataset.postId || null;
    // 수정 진입 시점의 원본 title/content를 저장해두고, 저장 시 변경 여부를 비교한다.
    snapshot = { title: els.title.value, content: els.content.value };
    window.PostEditor.init(els.content.value);

    panel.querySelector('[data-action="edit"]').addEventListener("click", enterEdit);
    panel.querySelector('[data-action="cancel"]').addEventListener("click", cancelEdit);
    panel.querySelector('[data-action="save"]').addEventListener("click", saveEdit);
    panel.querySelector('[data-action="delete"]').addEventListener("click", deletePost);
  }

  function setMode(mode) {
    panel.dataset.mode = mode;
    els.title.disabled = mode !== PostPanelMode.EDIT;
    window.PostEditor.setMode(mode);
  }

  function renderPost(data) {
    isUnsavedNewPost = false;
    postId = data.postId;
	window.AppState.setPostId(data.postId);
    panel.dataset.postId = postId;
    els.title.value = data.title ?? "";
    els.updatedAt.textContent = data.updatedAt ?? "";
    els.authorId.textContent = data.authorId ?? "";
    els.viewCount.textContent = data.viewCount ?? "";
    els.content.value = data.content ?? "";
    window.PostEditor.setValue(els.content.value);
    snapshot = { title: els.title.value, content: els.content.value };
    setMode(PostPanelMode.VIEW);
  }

  function resetPanel() {
    isUnsavedNewPost = false;
    postId = null;
    window.AppState.setPostId(null);
    panel.dataset.postId = "";
    els.title.value = "";
    els.updatedAt.textContent = "";
    els.authorId.textContent = "";
    els.viewCount.textContent = "";
    els.content.value = "";
    window.PostEditor.setValue("");
    snapshot = { title: "", content: "" };
    setMode(PostPanelMode.INITIAL);
  }

  async function loadPost(id) {
    try {
      const data = await window.Api.get(`/blog/api/post/${id}`);
      renderPost(data);
    } catch (error) {
      console.error(error);
      alert("게시글을 불러오는 중 오류가 발생했습니다: " + error.message);
      throw error; // 호출자가 실패를 알 수 있도록 다시 던진다
    }
  }

  function enterEdit() {
    if (!postId) return;
	window.AppState.setPostId(postId);
    setMode(PostPanelMode.EDIT);
  }

  function cancelEdit() {
    // 한 번도 저장된 적 없는 새 글이면, 서버의 빈 row는 그대로 둔 채(삭제하지
    // 않음) 빈 조회 화면을 보여주는 대신 목록으로 돌아간다.
    if (isUnsavedNewPost) {
      window.PostList.show();
      window.PostPanel.reset();
      window.PostPanel.hide();
      return;
    }

    els.title.value = snapshot.title;
    els.content.value = snapshot.content;
    window.PostEditor.setValue(snapshot.content);
    setMode(PostPanelMode.VIEW);

    if (!postId) {
      window.PostList.show();
      window.PostPanel.reset();
      window.PostPanel.hide();
      return;
    }
  }

  async function saveEdit() {

    const nextTitle = els.title.value;
    const nextContent = await window.PostEditor.getValue();
    els.content.value = nextContent;

    // 제목/내용 모두 바뀌지 않았다면 서버로 요청을 보내지 않고 조회 모드로만 복귀한다.
    if (nextTitle === snapshot.title && nextContent === snapshot.content) {
      setMode(PostPanelMode.VIEW);
      return;
    }

    // 수정
    const postId = window.AppState.getPostId();
    if(postId) {
      try {
      await window.Api.put(`/blog/api/post/${postId}`, {
        categoryId: window.AppState.getCategoryId(),
        title: nextTitle,
        content: nextContent
      });

      snapshot = { title: nextTitle, content: nextContent };
      isUnsavedNewPost = false;
      setMode(PostPanelMode.VIEW);
	  window.CategoryTree.reload();
      } catch (error) {
        console.error(error);
        alert("게시글 수정 중 오류가 발생했습니다: " + error.message);
      }
    }else {
      console.error("게시글 ID가 존재하지 않아 수정할 수 없습니다.");
    }
    
  }

  function createNewPost() {
    const body = {
      categoryId: window.AppState.getCategoryId(),
      title:"",
      content:""
    };

    window.Api.post("/blog/api/post", body)
      .then((newPostId) => {
        resetPanel();
        isUnsavedNewPost = true;
        window.AppState.setPostId(newPostId);
        postId = newPostId;
        setMode(PostPanelMode.EDIT);
        window.PostList.hide();
        window.PostPanel.show();
        els.title.focus();
      })
      .catch((err) => {
        alert("게시물 생성 실패: " + err.message);
      });
  }

  async function deletePost() {
    if (!postId) return;
    if (!confirm("이 게시글을 삭제하시겠습니까?")) return;

    const tree = window.jQuery && $.fn.jstree && $("#category-jstree").jstree(true);
    const node = tree && tree.get_node("post_" + postId);

    // 트리 노드가 있으면 jsTree의 delete_node 이벤트(category-tree.js)에 실제 삭제 API 호출과
    // 패널 초기화를 위임한다. 여기서 직접 fetch까지 하면 삭제 요청이 중복 전송된다.
    if (tree && node) {
      tree.delete_node(node);
      return;
    }

    try {
      await window.Api.delete(`/blog/api/post/${postId}`);
      resetPanel();
    } catch (error) {
      console.error(error);
      alert("게시글 삭제 중 오류가 발생했습니다: " + error.message);
    }
  }

  // 트리에서 이름을 바꿨을 때(category-tree.js) 패널의 제목과 dirty-check 기준값도 함께 갱신한다.
  // 편집 중(mode === EDIT)에는 사용자가 입력 중인 값을 덮어쓰지 않도록 건드리지 않는다.
  function syncTitle(title) {
    if (panel.dataset.mode === PostPanelMode.EDIT) return;
    els.title.value = title;
    snapshot.title = title;
  }

  function show() {
    detailSection.classList.remove("is-hidden");
  }

  function hide() {
    detailSection.classList.add("is-hidden");
  }

  // blog-app.js가 init()을 호출해 DOM을 바인딩한 뒤, category-tree.js/post-list.js가
  // load/reset/syncTitle/show/hide를 호출해 패널을 조작한다.
  return { init, load: loadPost, createNewPost, reset: resetPanel, syncTitle, show, hide };
})();
