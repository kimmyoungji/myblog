// #post-vditor에 Vditor 인스턴스를 하나만 만들어두고, view/edit 모드는
// disabled()/enable() 토글로 표현한다. 실제 데이터 저장소는 post-panel.js가 들고 있는
// 숨김 textarea(#post-content)이며, 이 모듈은 그 값을 Vditor로/에서 옮기는 역할만 한다.
window.PostEditor = (function () {
  let vditor = null;
  let ready;
  let readyResolve;

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
	      url: '/blog/api/file/upload', // Your server-side upload endpoint
	      max: 12 * 1024 * 1024,    // Max file size in bytes (e.g., 10MB)
	      fieldName: 'file',        // Form field name for the file
	      multiple: false,
		  headers: {
	        Authorization: 'Bearer YOUR_TOKEN'
	      },
		  format: function(files, responseText) {
	          	debugger;
	          	const response = JSON.parse(responseText);
	          	const vditorResponse = {
		            code: response.success ? 1 : 0,
		            msg: response.message ?? "",
		            data: response.data
				};
				console.log("vditorResponse: ", vditorResponse);
				return JSON.stringify(vditorResponse);
		  },
	      /* success: (ed, msg) => {
	      	console.log('Upload success:', msg);
	      }, */
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
      vditor.enable();
      vditor.focus();
    } else {
      vditor.disabled();
      renderPreview(vditor.getValue()); // 편집 중 입력한 내용을 그대로 반영
    }
  }

  return { init, getValue, setValue, setMode };
})();
