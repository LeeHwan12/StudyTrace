import { Routes, Route, useLocation } from "react-router-dom";
import Nav from "./components/Nav";
import Home from "./components/Home";
import MyPage from "./components/Mypage";
import LoginModal from "./components/LoginModal";
import Register from "./components/Register";
import Footer from "./components/Footer";
import TimeLineDetail from "./components/home/TimelineDetail";
import "./App.css";
import "@fortawesome/fontawesome-free/css/all.min.css";

export default function App() {
  const location = useLocation();
  const state = location.state;
  const backgroundLocation = state?.backgroundLocation;

  return (
    <>
      <Nav />

      {/* ✅ 1) 메인 Routes: /login을 항상 등록 */}
      <Routes location={backgroundLocation || location}>
        <Route path="/" element={<Home />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<LoginModal />} /> 
        <Route path="/timeline/:timelineId" element={<TimeLineDetail />} />
      </Routes>

      {/* ✅ 2) backgroundLocation이 있을 때만 모달을 "위에" 한 번 더 렌더 */}
      {backgroundLocation && (
        <Routes>
          <Route path="/login" element={<LoginModal />} />
        </Routes>
      )}

      <Footer />
    </>
  );
}
