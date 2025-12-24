import { apiFetch } from "./api";

/* 프로필 수정 */
export async function changeProfile({ nickname, email }) {
  return apiFetch("/api/mypage", {
    method: "PATCH",
    body: {
      nickname,
      email,
    },
  });
}

/* 비밀번호 변경 */
export async function changePassword({
  oldPassword,
  newPassword,
  confirmNewPassword,
}) {
  return apiFetch("/api/mypage/password", { // ✅ 경로 수정
    method: "PATCH",
    body: {
      oldPassword,
      newPassword,
      confirmNewPassword,
    },
  });
}

/* 활성/비활성 토글 */
export async function changeActive() {
  return apiFetch("/api/mypage/active", {
    method: "PATCH",
  });
}
