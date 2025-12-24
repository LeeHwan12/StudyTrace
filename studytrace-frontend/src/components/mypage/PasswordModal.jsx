import "../mypage/ModalBase.css";
import { useEffect, useState } from "react";
import { changePassword } from "../../lib/myPage";
import { useAuth } from "../../context/AuthContext";

export default function PasswordModal({ onClose }) {
  const { logout } = useAuth(); // 비번 변경 후 강제 로그아웃 정책이면 사용
  const [form, setForm] = useState({
    oldPassword: "",
    newPassword: "",
    confirmNewPassword: "",
  });
  const [err, setErr] = useState("");
  const [message, setMessage] = useState("");

  const close = () => {
    setErr("");
    setMessage("");
    onClose?.();
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr("");
    setMessage("");

    // 프론트 1차 검증
    if (form.newPassword !== form.confirmNewPassword) {
      setErr("새 비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      await changePassword(form);
      setMessage("비밀번호가 변경되었습니다.");

      // ✅ 정책 선택:
      // 비번 변경 후 세션 무효/재로그인 요구가 흔함 → 원하면 아래 활성화
      // await logout?.();

      // 바로 닫고 싶으면 close();
    } catch (e2) {
      setErr(e2?.message ?? "비밀번호 변경 실패");
    }
  };

  useEffect(() => {
    const onKey = (e) => {
      if (e.key === "Escape") close();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="modal-overlay" onClick={close}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <h2>비밀번호 변경</h2>
        <p className="desc">현재 비밀번호 확인 후 새 비밀번호로 변경합니다.</p>

        <form onSubmit={onSubmit}>
          <div className="input-group">
            <i className="fa-solid fa-lock" />
            <input
              name="oldPassword"
              type="password"
              placeholder="현재 비밀번호"
              value={form.oldPassword}
              onChange={(e) => setForm({ ...form, oldPassword: e.target.value })}
              autoFocus
            />
          </div>

          <div className="input-group">
            <i className="fa-solid fa-key" />
            <input
              name="newPassword"
              type="password"
              placeholder="새 비밀번호 (8~30자)"
              value={form.newPassword}
              onChange={(e) => setForm({ ...form, newPassword: e.target.value })}
            />
          </div>

          <div className="input-group">
            <i className="fa-solid fa-key" />
            <input
              name="confirmNewPassword"
              type="password"
              placeholder="새 비밀번호 확인"
              value={form.confirmNewPassword}
              onChange={(e) => setForm({ ...form, confirmNewPassword: e.target.value })}
            />
          </div>

          <div className="modal-actions">
            <button className="btn-primary" type="submit">변경</button>
            <button className="btn-secondary" type="button" onClick={close}>닫기</button>
          </div>

          {message && <p className="success">{message}</p>}
          {err && <p className="error">{err}</p>}
        </form>
      </div>
    </div>
  );
}
