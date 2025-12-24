import { useState } from "react";
import UpdateProfileModal from "./mypage/UpdateProfileModal";
import PasswordModal from "./mypage/PasswordModal";
import ActiveModal from "./mypage/ActiveModal";
import { useAuth } from "../context/AuthContext";
import "../css/mypage.css";

export default function MyPage() {
  const { user } = useAuth();
  const [open, setOpen] = useState(null); // "profile" | "password" | "active" | null

  if (!user) return null; // 로딩 중 / 비로그인 방어

  return (
    <div className="mypage">
      <h1>마이페이지</h1>

      {/* ======================
          내 정보 카드
      ====================== */}
      <div className="mypage-card">
        <h2>내 정보</h2>

        <div className="info-row">
          <span className="info-label">아이디</span>
          <span className="info-value">{user.username}</span>
        </div>

        <div className="info-row">
          <span className="info-label">닉네임</span>
          <span className="info-value">{user.nickname}</span>
        </div>

    
        <div className="info-row">
          <span className="info-label">계정 상태</span>
          <span
            className={`status-badge ${
              user.active === "ACTIVE" ? "active" : "inactive"
            }`}
          >
            {user.active}
          </span>
        </div>

        <div className="mypage-actions">
          <button
            className="mypage-btn primary"
            onClick={() => setOpen("profile")}
          >
            프로필 수정
          </button>

          <button
            className="mypage-btn"
            onClick={() => setOpen("password")}
          >
            비밀번호 변경
          </button>

          <button
            className="mypage-btn danger"
            onClick={() => setOpen("active")}
          >
            {user.active === "ACTIVE" ? "비활성화" : "활성화"}
          </button>
        </div>
      </div>

      {/* ======================
          모달 영역
      ====================== */}
      {open === "profile" && (
        <UpdateProfileModal
          initialNickname={user.nickname}
          initialEmail={user.email}
          onClose={() => setOpen(null)}
        />
      )}

      {open === "password" && (
        <PasswordModal onClose={() => setOpen(null)} />
      )}

      {open === "active" && (
        <ActiveModal
          currentActive={user.active}
          onClose={() => setOpen(null)}
        />
      )}
    </div>
  );
}
