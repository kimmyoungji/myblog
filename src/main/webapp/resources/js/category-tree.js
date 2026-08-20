window.CategoryTree = (function () {
  // 카테고리 조회 요청. init()과 reload() 둘 다 여기서 데이터를 받아온다.
  async function fetchTreeData() {
    const treeData = await window.Api.get("/blog/api/category/jstree?haspost=true");

    // 예외처리: 비정상 응답 데이터(데이터 형태 검증)
    if (!Array.isArray(treeData)) {
      throw new Error("jsTree 데이터가 배열 형식이 아닙니다.");
    }

    return treeData;
  }

  async function init() {
    const categoryJstree = document.querySelector("#category-jstree");

    try {
      // 예외처리: 트리요소가 없는 경우
      if (!categoryJstree) {
        throw new Error("#category-jstree 요소를 찾을 수 없습니다.");
      }

      // 예외처리: jsTree 라이브러리 로드가 안된 경우
      if (
        typeof window.jQuery === "undefined" ||
        typeof $.fn.jstree !== "function"
      ) {
        throw new Error("jQuery 또는 jsTree가 로드되지 않았습니다.");
      }

      const treeData = await fetchTreeData();

      // 트리 그리기
      $(categoryJstree)
        .on("error.jstree", function (e, data) {
          console.error("jsTree 오류:", data);
        })
        .on("changed.jstree", async function (e, data) {
          // refresh() 중 dnd 플러그인이 내부적으로 deselect_all을 트리거할 때는
          // data.node가 없으므로 무시한다.
          if (!data.node) return;

          const isPost = data.node.type === "post";
          const id = data.node.id.replace(isPost ? "post_" : "category_", "");
          const endpoint =
            (isPost ? "/blog/api/post/" : "/blog/api/category/") + id;

          // 만약 게시물이면 게시물 상세 페이지를 보여주고.
          if (isPost) {
            window.AppState.setPostId(id);
			await window.PostPanel.load(id);
			window.PostList.hide();
            window.PostPanel.show();
          }
          // 만약 카테고리면 게시물 목록 페이지를 보여준다.
          else {
            window.AppState.setCategoryId(id);
            // 목록 화면으로 전환하므로 이전에 보던 게시글 id는 지운다.
            // (지우지 않으면 refresh.jstree 복원 로직이 목록 화면에서도
            // 옛 게시글 노드를 다시 선택해 패널이 튀어나온다)
            window.AppState.setPostId(null);
			await window.PostList.load(id);
			window.PostPanel.hide();
            window.PostList.show();
          }

          // 모바일 폭에서는 nav가 드로어이므로, 항목을 고르면 본문을 볼 수 있게 닫아준다.
          // responsive.css의 브레이크포인트(767px)와 반드시 같은 값을 유지할 것.
          if (window.innerWidth < 768) window.NavDrawer.close();
        })
        .on("create_node.jstree", function (e, data) {
          const tree = $("#category-jstree").jstree(true);
          const isPost = data.node.type === "post";
          let endpoint, body;

          if (isPost) {
            const parentNode = tree.get_node(data.node.parent);
            const parentIsPost = parentNode.type === "post";
            const categoryId = parentIsPost
              ? parentNode.original.data.categoryId
              : parentNode.id.replace("category_", "");
            const parentPostId = parentIsPost
              ? parentNode.id.replace("post_", "")
              : null;
            endpoint = "/blog/api/post";
            body = {
              categoryId: categoryId,
              parentPostId: parentPostId,
              sortSeq: data.position,
              title: data.node.text,
              content: "",
            };
          } else {
            const parentCategoryId =
              data.node.parent === "#"
                ? null
                : data.node.parent.replace("category_", "");
            endpoint = "/blog/api/category";
            body = {
              name: data.node.text,
              parentCategoryId: parentCategoryId,
              sortSeq: data.position,
            };
          }

          window.Api.post(endpoint, body)
            .then((newId) => {
              // 서버가 발급한 실제 id로 노드 id를 교체
              tree.set_id(data.node, (isPost ? "post_" : "category_") + newId);
            })
            .catch((err) => {
              alert("생성 실패: " + err.message);
              tree.delete_node(data.node); // 실패 시 UI 롤백
            });
        })
        .on("rename_node.jstree", function (e, data) {
          const isPost = data.node.type === "post";
          const id = data.node.id.replace(isPost ? "post_" : "category_", "");
          const endpoint =
            (isPost ? "/blog/api/post/" : "/blog/api/category/") +
            id +
            "/rename";
          const body = isPost ? { title: data.text } : { name: data.text };

          window.Api.patch(endpoint, body)
            .then(() => {
              // 현재 패널에 열려있는 게시물이면 제목과 dirty-check 기준값도 함께 갱신한다.
              // (안 해두면 패널에서 저장할 때 옛 제목으로 덮어써 버리거나, 취소 시 옛 제목으로 되돌아간다)
              const panel = document.querySelector("#post-panel");
              if (isPost && panel && panel.dataset.postId === id) {
                window.PostPanel.syncTitle(data.text);
              }
            })
            .catch((err) => {
              alert("이름 변경 실패: " + err.message);
              $("#category-jstree")
                .jstree(true)
                .set_text(data.node, data.old); // 실패 시 이전 이름으로 복구
            });
        })
        .on("delete_node.jstree", function (e, data) {
          const isPost = data.node.type === "post";
          const id = data.node.id.replace(isPost ? "post_" : "category_", "");
          const endpoint =
            (isPost ? "/blog/api/post/" : "/blog/api/category/") + id;

          window.Api.delete(endpoint)
            .then(() => {
              // 삭제한 게시물이 현재 열려있던 게시물이라면 패널을 초기화
              const panel = document.querySelector("#post-panel");
              if (isPost && panel && panel.dataset.postId === id) {
                window.PostPanel.reset();
              }
            })
            .catch((err) => {
              alert("삭제 실패: " + err.message);
              // 삭제 실패 시 트리를 다시 불러와서 복구 (delete_node는 undo가 번거로움)
              location.reload();
            });
        })
        .on("move_node.jstree", function (e, data) {
          const tree = $("#category-jstree").jstree(true);
          const isPost = data.node.type === "post";
          const id = data.node.id.replace(isPost ? "post_" : "category_", "");
          let endpoint, body;

          if (isPost) {
            const parentNode = tree.get_node(data.node.parent);
            const parentIsPost = parentNode.type === "post";
            const categoryId = parentIsPost
              ? parentNode.original.data.categoryId
              : parentNode.id.replace("category_", "");
            const parentPostId = parentIsPost
              ? parentNode.id.replace("post_", "")
              : null;
            endpoint = "/blog/api/post/" + id + "/relocate";
            body = {
              categoryId: categoryId,
              parentPostId: parentPostId,
              sortSeq: data.position,
            };
          } else {
            const parentCategoryId =
              data.node.parent === "#"
                ? null
                : data.node.parent.replace("category_", "");
            endpoint = "/blog/api/category/" + id + "/relocate";
            body = {
              parentCategoryId: parentCategoryId,
              sortSeq: data.position,
            };
          }

          window.Api.patch(endpoint, body)
            .catch((err) => {
              alert("이동 실패: " + err.message);
              // 삭제 실패 시 트리를 다시 불러와서 복구 (delete_node는 undo가 번거로움)
              location.reload();
            });
        })
        .jstree({
          core: {
            data: treeData,
            check_callback: true, // 트리 편집 동작을 실행 여부
          },
          // 우클릭 메뉴(생성/이름변경/삭제)와 드래그앤드롭 이동은 전부 쓰기 동작이라
          // 관리자에게만 노출한다. 일반 방문자는 트리를 읽기 전용으로만 본다.
          plugins: window.Auth.isAdmin() ? ["types", "contextmenu", "dnd"] : ["types"],
          types: {
            default: { icon: "jstree-icon-default" },
            category: {
              icon: "fa fa-folder",
              valid_children: ["category", "post"],
            },
            post: {
              icon: "fa fa-file-alt",
              valid_children: ["post"], // 게시글 아래엔 자식 노드 불가
            },
          },
          contextmenu: {
            items: function (node) {
              const tree = $("#category-jstree").jstree(true);
              const isCategory = node.type === "category";

              return {
                createChildCategory: {
                  label: "하위 카테고리 추가",
                  _disabled: !isCategory, // post 노드에는 하위 카테고리 생성 불가
                  action: function () {
                    const newNode = tree.create_node(node, {
                      text: "새 카테고리",
                      type: "category",
                    });
                    tree.edit(newNode); // 생성 즉시 이름 입력 모드로 전환
                  },
                },
                createChildPost: {
                  label: "하위 게시글 추가",
                  action: function () {
                    const newNode = tree.create_node(node, {
                      text: "새 게시글",
                      type: "post",
                    });
                    tree.edit(newNode);
                  },
                },
                rename: {
                  label: "이름 변경",
                  action: function () {
                    tree.edit(node);
                  },
                },
                remove: {
                  label: "삭제",
                  action: function () {
                    if (confirm(`"${node.text}"을(를) 삭제하시겠습니까?`)) {
                      tree.delete_node(node);
                    }
                  },
                },
              };
            },
          },
        });
    } catch (error) {
      console.error(error);

      if (categoryJstree) {
        categoryJstree.textContent = "카테고리를 불러오지 못했습니다.";
      }
    }
  }

  // 트리 밖(패널의 "새 글 작성" 등)에서 게시글/카테고리가 바뀐 경우, 서버 데이터를
  // 다시 받아와 트리를 새로 그린다. core.data가 정적 배열이라 refresh()만으로는
  // 새 데이터를 못 받아오므로, settings.core.data를 갈아끼운 뒤 refresh()를 호출한다.
  async function reload() {
    const tree = $("#category-jstree").jstree(true);
    if (!tree) return;

    try {
      // refresh 전에(=옛 트리가 살아있을 때) 현재 선택 노드를 구해둔다.
      const previousNode = tree.get_node(getSelectedNodeId());

      tree.settings.core.data = await fetchTreeData();

      // refresh()는 노드 로딩을 비동기 처리하므로 refresh.jstree 이후에 열어야
      // 한다. open_node는 존재하지 않는 id를 넘겨도 조용히 무시하므로, 본인과
      // 조상 id를 통째로 넘기면 새 트리에 살아있는 것까지만 자연스럽게 펼쳐진다.
      $("#category-jstree").one("refresh.jstree", function () {
        tree.open_node([previousNode.id, ...previousNode.parents]);
      });

      tree.refresh(false, true);
    } catch (error) {
      console.error(error);
    }
  }

  // 현재 선택된 노드의 id를 반환한다. 선택된 노드가 없으면 "#"을 반환한다.
  function getSelectedNodeId() {
    const tree = $("#category-jstree").jstree(true);
    return tree ? (tree.get_selected()[0] || "#") : "#";
  }

  function open_node(nodeId) {
    const tree = $("#category-jstree").jstree(true);
    if (!tree) return;

    tree.open_node(nodeId);
  }

  return { init, reload, getSelectedNodeId, open_node };
})();
