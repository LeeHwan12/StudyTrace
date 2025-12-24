import { useState } from "react";
import { register } from "../lib/auth";
import "../css/Register.css";

export default function Register() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    passwordConfirm: "",
    email: "",
    nickname: "",
  });

  const [message, setMessage] = useState("");
  const [err, setErr] = useState("");

  const changeHandle = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const submitHandle = async (e) => {
    e.preventDefault();
    setErr("");
    setMessage("");

    if (form.password !== form.passwordConfirm) {
      setErr("비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      await register(form);
      setMessage("회원가입이 완료되었습니다.");
    } catch (e) {
      setErr(e?.message ?? "회원가입 실패");
    }
  };

  return (
    <div className="register-page">
      <div className="register-card">
        <h2>회원가입</h2>

        <form onSubmit={submitHandle}>
          {/* 아이디 */}
          <div className="input-group">
            <i className="fa-solid fa-user"></i>
            <input
              type="text"
              name="username"
              placeholder="아이디"
              value={form.username}
              onChange={changeHandle}
            />
          </div>

          {/* 비밀번호 */}
          <div className="input-group">
            <i className="fa-solid fa-lock"></i>
            <input
              type="password"
              name="password"
              placeholder="비밀번호"
              value={form.password}
              onChange={changeHandle}
            />
          </div>

          {/* 비밀번호 확인 */}
          <div className="input-group">
            <i className="fa-solid fa-lock"></i>
            <input
              type="password"
              name="passwordConfirm"
              placeholder="비밀번호 확인"
              value={form.passwordConfirm}
              onChange={changeHandle}
            />
          </div>

          {/* 닉네임 */}
          <div className="input-group">
            <i className="fa-solid fa-id-badge"></i>
            <input
              type="text"
              name="nickname"
              placeholder="닉네임"
              value={form.nickname}
              onChange={changeHandle}
            />
          </div>

          {/* 이메일 */}
          <div className="input-group">
            <i className="fa-solid fa-envelope"></i>
            <input
              type="email"
              name="email"
              placeholder="이메일"
              value={form.email}
              onChange={changeHandle}
            />
          </div>

          <button type="submit">회원가입</button>
        </form>

        {message && <p className="success">{message}</p>}
        {err && <p className="error">{err}</p>}
      </div>
    </div>
  );
}
