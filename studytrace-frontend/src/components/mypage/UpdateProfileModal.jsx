import "../mypage/ModalBase.css";
import { useEffect, useState } from "react";
import { changeProfile } from "../../lib/myPage"; // 네가 만든 API 파일 경로에 맞춰 수정
import { useAuth } from "../../context/AuthContext";

export default function UpdateProfileModal({ initialNickname = "", initialEmail = "", onClose }) {
  const { refreshMe } = useAuth(); // AuthContext에 me 재호출 함수가 있으면 추천(없으면 제거)
  const [form, setForm] = useState({ nickname: initialNickname, email: initialEmail });
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
    try {
      await changeProfile(form);
      setMessage("프로필이 수정되었습니다.");
      if (refreshMe) await refreshMe(); // Nav 닉네임 즉시 반영용
      // 성공 후 바로 닫고 싶으면 close(); 로 바꾸면 됨
    } catch (e2) {
      setErr(e2?.message ?? "프로필 수정 실패");
    }
  };

  // ESC 닫기 / Enter 제출은 form onSubmit로 충분 (ESC만 추가)
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
        <h2>프로필 수정</h2>
        <p className="desc">닉네임과 이메일을 변경할 수 있어요.</p>

        <form onSubmit={onSubmit}>
          <div className="input-group">
            <i className="fa-solid fa-id-badge" />
            <input
              name="nickname"
              placeholder="닉네임"
              value={form.nickname}
              onChange={(e) => setForm({ ...form, nickname: e.target.value })}
              autoFocus
            />
          </div>

          <div className="input-group">
            <i className="fa-solid fa-envelope" />
            <input
              name="email"
              type="email"
              placeholder="이메일"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />
          </div>

          <div className="modal-actions">
            <button className="btn-primary" type="submit">저장</button>
            <button className="btn-secondary" type="button" onClick={close}>닫기</button>
          </div>

          {message && <p className="success">{message}</p>}
          {err && <p className="error">{err}</p>}
        </form>
      </div>
    </div>
  );
}
