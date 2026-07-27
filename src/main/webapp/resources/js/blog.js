// 게시글 영역(#post-panel)의 상태. main.jsp의 data-mode 초기값과 문자열이 일치해야 한다.
const PostPanelMode = Object.freeze({
  INITIAL: "initial",
  VIEW: "view",
  EDIT: "edit",
});

document.addEventListener("DOMContentLoaded", function () {
  const panel = document.querySelector("#post-panel");
  if (!panel) return;

  const els = {
    title: panel.querySelector("#post-title"),
    updatedAt: panel.querySelector("#post-updatedAt"),
    authorId: panel.querySelector("#post-authorId"),
    viewCount: panel.querySelector("#post-viewCount"),
    content: panel.querySelector("#post-content"),
  };

  let postId = panel.dataset.postId || null;
  // 수정 진입 시점의 원본 title/content를 저장해두고, 저장 시 변경 여부를 비교한다.
  let snapshot = { title: els.title.value, content: els.content.value };

  function setMode(mode) {
    panel.dataset.mode = mode;
    els.content.disabled = mode !== PostPanelMode.EDIT;
    els.title.disabled = mode !== PostPanelMode.EDIT;
  }

  function renderPost(data) {
    postId = data.postId;
    panel.dataset.postId = postId;
    els.title.value = data.title ?? "";
    els.updatedAt.textContent = data.updatedAt ?? "";
    els.authorId.textContent = data.authorId ?? "";
    els.viewCount.textContent = data.viewCount ?? "";
    els.content.value = data.content ?? "";
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
    els.content.focus();
  }

  function cancelEdit() {
    els.title.value = snapshot.title;
    els.content.value = snapshot.content;
    setMode(PostPanelMode.VIEW);
  }

  async function saveEdit() {
    if (!postId) return;

    const nextTitle = els.title.value;
    const nextContent = els.content.value;

    // 제목/내용 모두 바뀌지 않았다면 서버로 요청을 보내지 않고 조회 모드로만 복귀한다.
    if (nextTitle === snapshot.title && nextContent === snapshot.content) {
      setMode(PostPanelMode.VIEW);
      return;
    }

    try {
      const response = await fetch(`/blog/api/post/${postId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          title: nextTitle,
          content: nextContent,
        }),
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

  async function deletePost() {
    if (!postId) return;
    if (!confirm("이 게시글을 삭제하시겠습니까?")) return;

    const tree = window.jQuery && $.fn.jstree && $("#category-jstree").jstree(true);
    const node = tree && tree.get_node("post_" + postId);

    // 트리 노드가 있으면 jsTree의 delete_node 이벤트(nav.jsp)에 실제 삭제 API 호출과
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

  // 트리에서 이름을 바꿨을 때(nav.jsp) 패널의 제목과 dirty-check 기준값을 함께 갱신한다.
  // 편집 중(mode === EDIT)에는 사용자가 입력 중인 값을 덮어쓰지 않도록 건드리지 않는다.
  function syncTitle(title) {
    if (panel.dataset.mode === PostPanelMode.EDIT) return;
    els.title.value = title;
    snapshot.title = title;
  }

  panel.querySelector('[data-action="edit"]').addEventListener("click", enterEdit);
  panel.querySelector('[data-action="cancel"]').addEventListener("click", cancelEdit);
  panel.querySelector('[data-action="save"]').addEventListener("click", saveEdit);
  panel.querySelector('[data-action="delete"]').addEventListener("click", deletePost);

  // nav.jsp의 jsTree 선택/이름변경 이벤트 등 외부에서 패널을 조작할 때 사용
  window.PostPanel = { load: loadPost, reset: resetPanel, syncTitle };
});
