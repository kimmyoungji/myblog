// 게시글 영역(#post-panel)의 상태. detail.jsp의 data-mode 초기값과 문자열이 일치해야 한다.
const PostPanelMode = Object.freeze({
  INITIAL: "initial",
  VIEW: "view",
  EDIT: "edit",
});

window.PostPanel = (function () {
  let panel, detailSection, els, postId, snapshot;

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
    postId = data.postId;
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
    postId = null;
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
      const response = await fetch(`/blog/api/post/${id}`);
      const result = await response.json();

      if (!response.ok || !result.success) {
        throw new Error(result.message || "게시글 조회 요청 실패");
      }

      renderPost(result.data);
    } catch (error) {
      console.error(error);
      alert("게시글을 불러오는 중 오류가 발생했습니다: " + error.message);
    }
  }

  function enterEdit() {
    if (!postId) return;
    setMode(PostPanelMode.EDIT);
  }

  function cancelEdit() {
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

    // 게시글 생성/수정 공통 body
    const body = {
      categoryId: window.AppState.getCategoryId(),
      title: nextTitle,
      content: nextContent
    };

    // 게시글 생성/수정 공통 endpoint
    const endpoint = postId ? `/blog/api/post/${postId}` : "/blog/api/post";

    // 수정
    if(postId != null || postId != undefined) {
      try {
      const response = await fetch(endpoint, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      const result = await response.json();

      if (!response.ok || !result.success) {
        throw new Error(result.message || "게시글 수정 실패");
      }

      snapshot = { title: nextTitle, content: nextContent };
      setMode(PostPanelMode.VIEW);
      } catch (error) {
        console.error(error);
        alert("게시글 수정 중 오류가 발생했습니다: " + error.message);
      }
    }
    // 생성
    else {
      fetch(endpoint, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(body),
        })
          .then((res) => res.json())
          .then(async (result) => {
            if (!result.success) throw new Error(result.message);
            // 생성 성공 시 트리를 다시 불러와 새 게시글 노드가 보이게 하고, 패널을 조회 모드로 전환한다.
            await window.CategoryTree.reload();

            postId = result.data.postId;
            panel.dataset.postId = postId;
            
            window.AppState.setPostId(postId);
            snapshot = { title: nextTitle, content: nextContent };
            setMode(PostPanelMode.VIEW);
          })
          .catch((err) => {
            alert("생성 실패: " + err.message);
          });
    }
    
  }

  function createNewPost() {
    window.PostList.hide();
    window.PostPanel.show();
    resetPanel();
    setMode(PostPanelMode.EDIT);
    els.title.focus();
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
      const response = await fetch(`/blog/api/post/${postId}`, {
        method: "DELETE",
      });
      const result = await response.json();

      if (!response.ok || !result.success) {
        throw new Error(result.message || "게시글 삭제 실패");
      }

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
