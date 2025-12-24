import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../css/Nav.css";

export default function Nav() {
  const location = useLocation();
  const { user, loading, logout } = useAuth();

  if (loading) {
    return (
      <nav className="nav">
        <div className="nav-left">
          <Link to="/" className="logo">StudyTrace</Link>
        </div>
      </nav>
    );
  }

  return (
    <nav className="nav">
      <div className="nav-left">
        <Link to="/" className="logo">StudyTrace</Link>
      </div>

      <ul className="menu">
        {!user ? (
          <>
            <li>
              <Link
                to="/login"
                state={{ backgroundLocation: location }}   // ✅ 현재 화면을 배경으로 저장
              >
                로그인
              </Link>
            </li>
            <li>
              <Link to="/register">회원가입</Link>
            </li>
          </>
        ) : (
          <>
            <li><Link to="/mypage">마이페이지</Link></li>
            <li>
              <button className="logout-btn" onClick={logout}>
                로그아웃
              </button>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
}
