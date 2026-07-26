<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags" %>    

<!DOCTYPE html>
<aside class="layout__nav">
    <nav class="category-nav" aria-label="블로그 카테고리">
    	<%-- <ul class="category-tree">
            <c:forEach var="category" items="${categoryTreeWithPosts}">
                <app:categoryTree category="${category}" />
            </c:forEach>
        </ul>  --%>
        <div id="category-jstree"></div>
    </nav>
    <script>
    		document.addEventListener("DOMContentLoaded", initializeCategoryTree);

    		/* 카테고리 트리 초기화 */
    		async function initializeCategoryTree() {
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
				
    		    // 카테고리 조회 요청
    		    const response = await fetch(
    		      "/blog/api/category/jstree?haspost=true"
    		    );
				
    		    // 예외처리: 실패 응답
    		    if (!response.ok) {
    		      throw new Error(`카테고리 조회 실패: ${response.status}`);
    		    }
				
    		    // 응답 처리
    		    const result = await response.json();
    		    const treeData = result.data ?? result;
    		    
    		    // 예외처리: 비정상 응답 데이터(데이터 형태 검증)
    		    if (!Array.isArray(treeData)) {
    		      throw new Error("jsTree 데이터가 배열 형식이 아닙니다.");
    		    }
				
    		    // 트리 그리기
    		    $(categoryJstree)
    		      .on("ready.jstree", function () {
    		        console.log("jsTree 생성 완료");
    		      })
    		      .on("error.jstree", function (e, data) {
    		        console.error("jsTree 오류:", data);
    		      })
	    		      .on("create_node.jstree", function (e, data) {
	    		    	  	const tree = $('#category-jstree').jstree(true);
	    		    	  	const isPost = data.node.type === "post";
	    		    	  	let endpoint, body;

	    		    	  	if (isPost) {
	    		    	  	  const parentNode = tree.get_node(data.node.parent);
	    		    	  	  const parentIsPost = parentNode.type === "post";
	    		    	  	  const categoryId = parentIsPost
	    		    	  	    ? parentNode.original.data.categoryId
	    		    	  	    : parentNode.id.replace("category_", "");
	    		    	  	  const parentPostId = parentIsPost ? parentNode.id.replace("post_", "") : null;
	    		    	  	  endpoint = '/blog/api/post';
	    		    	  	  body = { title: data.node.text, categoryId: categoryId, parentPostId: parentPostId, sortSeq: data.position, content: '' };
	    		    	  	} else {
	    		    	  	  const parentCategoryId = data.node.parent === "#" ? null : data.node.parent.replace("category_", "");
	    		    	  	  endpoint = '/blog/api/category';
	    		    	  	  body = { name: data.node.text, parentCategoryId: parentCategoryId, sortSeq: data.position };
	    		    	  	}

		   		    	fetch(endpoint, {
		   		    	  method: 'POST',
		   		    	  headers: { 'Content-Type': 'application/json' },
		   		    	  body: JSON.stringify(body)
		   		    	})
		   		    	.then(res => res.json())
		   		    	.then(result => {
		   		    	  if (!result.success) throw new Error(result.message);
		   		    	  // 서버가 발급한 실제 id로 노드 id를 교체
		   		    	  tree.set_id(data.node, (isPost ? "post_" : "category_") + result.data);
		   		    	})
		   		    	.catch(err => {
		   		    	  alert("생성 실패: " + err.message);
		   		    	  tree.delete_node(data.node); // 실패 시 UI 롤백
		   		    	});
			  })
			  .on("rename_node.jstree", function (e, data) {
				  	const isPost = data.node.type === "post";
				  	const id = data.node.id.replace(isPost ? "post_" : "category_", "");
				  	const endpoint = (isPost ? '/blog/api/post/' : '/blog/api/category/') + id;
				  	const body = isPost ? { title: data.text } : { name: data.text };

				    fetch(endpoint, {
				      method: 'PATCH',
				      headers: { 'Content-Type': 'application/json' },
				      body: JSON.stringify(body)
				    })
				    .then(res => res.json())
				    .then(result => {
				      if (!result.success) throw new Error(result.message);
				    })
				    .catch(err => {
				      alert("이름 변경 실패: " + err.message);
				      $('#category-jstree').jstree(true).set_text(data.node, data.old); // 실패 시 이전 이름으로 복구
				    });
			  })
			  .on("delete_node.jstree", function (e, data) {
					  	const isPost = data.node.type === "post";
					  	const id = data.node.id.replace(isPost ? "post_" : "category_", "");
					  	const endpoint = (isPost ? '/blog/api/post/' : '/blog/api/category/') + id;

					    fetch(endpoint, { method: 'DELETE' })
					      .then(res => res.json())
					      .then(result => {
					        if (!result.success) throw new Error(result.message);
					      })
					      .catch(err => {
					        alert("삭제 실패: " + err.message);
					        // 삭제 실패 시 트리를 다시 불러와서 복구 (delete_node는 undo가 번거로움)
					        location.reload();
					      });
				  })
    		      .jstree({
    		        core: {
    		          data: treeData,
    		          check_callback: true // 트리 편집 동작을 실행 여부 
    		        },
    		        plugins: ["types", "contextmenu"],
    		        types: {
    		          "default": { icon: "jstree-icon-default" },
    		          "category": {
    		            icon: "fa fa-folder",
    		            valid_children: ["category", "post"]
    		          },
    		          "post": {
    		            icon: "fa fa-file-alt",
    		            valid_children: ["post"]   // 게시글 아래엔 자식 노드 불가
    		          }
    		        },
    		        contextmenu: {
   		        	  items: function (node) {
   		        	    const tree = $('#category-jstree').jstree(true);
   		        	    const isCategory = node.type === "category";

   		        	    return {
   		        	      createChildCategory: {
   		        	        label: "하위 카테고리 추가",
   		        	        _disabled: !isCategory, // post 노드에는 하위 카테고리 생성 불가
   		        	        action: function () {
   		        	          const newNode = tree.create_node(node, { text: "새 카테고리", type: "category" });
   		        	          tree.edit(newNode); // 생성 즉시 이름 입력 모드로 전환
   		        	        }
   		        	      },
   		        	      createChildPost: {
						  	label: "하위 게시글 추가",
						  	action: function () {
							  const newNode = tree.create_node(node, { text: "새 게시글", type: "post" });
							  tree.edit(newNode);
						  	}
   		        	      },
   		        	      rename: {
   		        	        label: "이름 변경",
   		        	        action: function () {
   		        	          tree.edit(node);
   		        	        }
   		        	      },
   		        	      remove: {
   		        	        label: "삭제",
   		        	        action: function () {
   		        	          if (confirm(`"${node.text}"을(를) 삭제하시겠습니까?`)) {
   		        	            tree.delete_node(node);
   		        	          }
   		        	        }
   		        	      }
   		        	    };
   		        	  }
   		        	}
    		      });
    		  } catch (error) {
    		    console.error(error);

    		    if (categoryJstree) {
    		      categoryJstree.textContent = "카테고리를 불러오지 못했습니다.";
    		    }
    		  }
    		}
    </script>
</aside>