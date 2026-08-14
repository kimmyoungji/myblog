// #post-vditor에 Vditor 인스턴스를 하나만 만들어두고, view/edit 모드는
// disabled()/enable() 토글로 표현한다. 실제 데이터 저장소는 post-panel.js가 들고 있는
// 숨김 textarea(#post-content)이며, 이 모듈은 그 값을 Vditor로/에서 옮기는 역할만 한다.
window.PostEditor = (function () {
  let vditor = null;
  let ready;
  let readyResolve;

  // vditor 인스턴스는 앱 시작 시 한 번만 만들어져 여러 게시글에서 재사용되므로,
  // 업로드 url은 고정값이 아니라 그때그때의 postId로 다시 조립해야 한다.
  function buildUploadUrl() {
    return "/blog/api/file/upload/vditor/" + window.AppState.getPostId();
  }

  function init(initialValue) {
    ready = new Promise((resolve) => {
      readyResolve = resolve;
    });

    try{
      vditor = new Vditor("post-vditor", {
      mode: "ir", // wysiwyg, ir(instant rendering), sv(split view)
      height: 320,
      lang: "en_US",
      placeholder: "띵거가 말하지 못한 것들...",
      cache: { enable: false },
      after: () => {
        vditor.setValue(initialValue); 
        vditor.disabled(); 
        renderPreview(initialValue); 
        readyResolve();
      },
      upload: {
	      url: buildUploadUrl(), // 초기값. 실제 업로드 시점의 postId는 setMode(edit)에서 다시 동기화한다.
	      max: 12 * 1024 * 1024,    // Max file size in bytes (e.g., 10MB)
	      fieldName: 'file',        // Form field name for the file
	      multiple: false,
		  format: function(files, responseText) {
	          	const response = JSON.parse(responseText);
	          	const vditorResponse = {
		            code: response.success ? 1 : 0,
		            msg: response.message ?? "",
		            data: response.data
				};
				return JSON.stringify(vditorResponse);
		  },
	      error: (msg) => {
	      	console.error('Upload failed:', msg);
	      }
	    }
    });
    }catch(e) {
      console.error("Vditor 초기화 중 오류 발생:", e);
    }
  }

  function renderPreview(value) {
    const previewEl = document.querySelector("#post-preview");
    Vditor.preview(previewEl, value || "", {});
  }

  async function getValue() {
    await ready;
    return vditor.getValue();
  }

  async function setValue(value) {
    await ready;
    vditor.setValue(value || "");
    renderPreview(value || "");
  }

  async function setMode(mode) {
    await ready;
    if (mode === "edit") {
      // 편집 진입 시점의 postId로 업로드 url을 다시 맞춰둔다.
      vditor.vditor.options.upload.url = buildUploadUrl();
      vditor.enable();
      vditor.focus();
    } else {
      vditor.disabled();
      renderPreview(vditor.getValue()); // 편집 중 입력한 내용을 그대로 반영
    }
  }

  return { init, getValue, setValue, setMode };
})();
