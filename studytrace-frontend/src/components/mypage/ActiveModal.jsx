import "../mypage/ModalBase.css";
import { useEffect, useState } from "react";
import { changeActive } from "../../lib/myPage";
import { useAuth } from "../../context/AuthContext";

export default function ActiveModal({ currentActive, onClose }) {
  const { refreshMe, logout } = useAuth(); // 비활성화 후 로그아웃 정책이면 사용
  const [err, setErr] = useState("");
  const [message, setMessage] = useState("");

  const close = () => {
    setErr("");
    setMessage("");
    onClose?.();
  };

  const onConfirm = async () => {
    setErr("");
    setMessage("");
    try {
      await changeActive();
      setMessage("계정 상태가 변경되었습니다.");

      if (refreshMe) await refreshMe();

      // ✅ 정책 선택:
      // INACTIVE로 바꾸면 보통 즉시 로그아웃 처리
      // if (currentActive === "ACTIVE") await logout?.();

      // 바로 닫고 싶으면 close();
    } catch (e2) {
      setErr(e2?.message ?? "상태 변경 실패");
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

  const nextLabel = currentActive === "ACTIVE" ? "비활성화" : "활성화";
  const desc =
    currentActive === "ACTIVE"
      ? "계정을 비활성화하면 일부 기능 사용이 제한될 수 있어요."
      : "계정을 다시 활성화합니다.";

  return (
    <div className="modal-overlay" onClick={close}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <h2>계정 {nextLabel}</h2>
        <p className="desc">{desc}</p>

        <div className="modal-actions">
          <button className="btn-danger" type="button" onClick={onConfirm}>
            {nextLabel}하기
          </button>
          <button className="btn-secondary" type="button" onClick={close}>
            닫기
          </button>
        </div>

        {message && <p className="success">{message}</p>}
        {err && <p className="error">{err}</p>}
      </div>
    </div>
  );
}
