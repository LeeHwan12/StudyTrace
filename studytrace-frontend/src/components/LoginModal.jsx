import "../css/LoginModal.css";
import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useLocation, useNavigate } from "react-router-dom";

export default function LoginModal() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [form, setForm] = useState({ username: "", password: "" });
  const [err, setErr] = useState("");

  const close = () => {
    if (location.state?.backgroundLocation) navigate(-1);
    else navigate("/");
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr("");
    try {
      await login(form);
      close();
    } catch (e2) {
      setErr(e2?.message ?? "로그인 실패");
    }
  };

  /* ESC / Enter */
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === "Escape") close();
      if (e.key === "Enter") onSubmit(e);
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [form]);

  return (
    <div className="modal-overlay" onClick={close}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <h2>로그인</h2>

        <form onSubmit={onSubmit}>
          {/* 아이디 */}
          <div className="input-group">
            <i className="fa-solid fa-user"></i>
            <input
              name="username"
              placeholder="아이디"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              autoFocus
            />
          </div>

          {/* 비밀번호 */}
          <div className="input-group">
            <i className="fa-solid fa-lock"></i>
            <input
              name="password"
              type="password"
              placeholder="비밀번호"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
            />
          </div>

          <button type="submit">로그인</button>
          {err && <p className="error">{err}</p>}
        </form>

        <button type="button" className="close-btn" onClick={close}>
          닫기
        </button>
      </div>
    </div>
  );
}
