// 서버 API 호출 공통 헬퍼. fetch + JSON 파싱 + ApiResponse(success/message/data)
// 확인을 한 곳에서 처리한다. 각 모듈은 성공 시의 data만 받고, 실패 시 던져지는
// Error를 잡아 처리하면 된다.
window.Api = (function () {
  async function request(url, options) {
    const response = await fetch(url, options);
    const result = await response.json();

    if (!response.ok || !result.success) {
      throw new Error(result.message || "요청 처리 중 오류가 발생했습니다");
    }

    return result.data;
  }

  function withJsonBody(method, url, body) {
    return request(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
  }

  return {
    get: (url) => request(url),
    post: (url, body) => withJsonBody("POST", url, body),
    put: (url, body) => withJsonBody("PUT", url, body),
    patch: (url, body) => withJsonBody("PATCH", url, body),
    delete: (url, body) =>
      body === undefined ? request(url, { method: "DELETE" }) : withJsonBody("DELETE", url, body),
  };
})();
