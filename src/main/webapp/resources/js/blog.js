document.addEventListener("DOMContentLoaded", function () {
	document.querySelectorAll(".post-item").forEach(function (postItem) {
	  postItem.addEventListener("click", async function (e) {
	    try {	
		  e.stopPropagation();
		  
		  console.log("postItem click event occured!")
		  console.log(e.currentTarget);
		  		
	      const target = e.currentTarget;

	      // 예: id="post_3"
	      const postItemId = target.id;
	      const postId = postItemId.split("_")[1];

	      const response = await fetch(`/api/blog/posts/${postId}`, {
	        method: "GET"
	      });

	      if (!response.ok) {
	        throw new Error("게시글 조회 요청 실패");
	      }

	      const result = await response.json();

	      // ApiResponse<Post> S구조라면 result.data 안에 Post가 있음
	      const data = result.data;

	      document.querySelector("#post-title").value = data.title;
	      document.querySelector("#post-updatedAt").value = data.updatedAt;
	      document.querySelector("#post-authorId").value = data.authorId;
	      document.querySelector("#post-viewCount").value = data.viewCount;
	      document.querySelector("#post-content").textContent = data.content;

	    } catch (error) {
	      console.error(error);
	      alert("게시글을 불러오는 중 오류가 발생했습니다.");
	    }
	  });
	});
});
