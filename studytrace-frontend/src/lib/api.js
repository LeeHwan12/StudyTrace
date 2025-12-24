const BASE_URL = "";

export async function apiFetch(path, options = {}) {
  const url = `${BASE_URL}${path}`;

  const defaultOptions = {
    credentials: "include",
    headers: {
      ...(options.body instanceof FormData ? {} : { "Content-Type": "application/json" }),
      ...(options.headers || {}),
    },
    ...options,
  };

  if (
    defaultOptions.body &&
    typeof defaultOptions.body === "object" &&
    !(defaultOptions.body instanceof FormData)
  ) {
    defaultOptions.body = JSON.stringify(defaultOptions.body);
  }

  const res = await fetch(url, defaultOptions);

  // 에러 처리
  if (!res.ok) {
    let errorMessage = `HTTP Error: ${res.status}`;
    try {
      const ct = res.headers.get("content-type") || "";
      if (ct.includes("application/json")) {
        const errorData = await res.json();
        errorMessage = errorData.message || errorMessage;
      } else {
        const text = await res.text();
        if (text) errorMessage = text;
      }
    } catch {}
    throw new Error(errorMessage);
  }

  // 성공인데 바디가 없는 경우(204 or 200 empty)
  if (res.status === 204) return null;

  const contentType = res.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    // 빈 JSON 바디면 여기서도 예외 날 수 있어서 text로 한번 읽는 방식이 더 안전
    const text = await res.text();
    return text ? JSON.parse(text) : null;
  }

  // JSON이 아니면 text로
  const text = await res.text();
  return text || null;
}

