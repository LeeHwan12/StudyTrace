import "../css/Home.css";
import WeeklyDonut from "./dashboard/WeeklyDonut";
import TopicBar from "./dashboard/TopicBar";
import TrendLine from "./dashboard/TrendLine";
import DemoTimeline from "./home/DemoTimeline";
import HeroShowcaseSlider from "./home/HeroShowcaseSlider";
import DashboardSlider from "./dashboard/DashboardSlider";

// ✅ Font Awesome icons
import {
  faPenToSquare,
  faBullseye,
  faArrowsRotate,
  faArrowTrendUp,
} from "@fortawesome/free-solid-svg-icons";

export default function Home() {
  const isLoggedIn = true;

  const showcaseSlides = [
  
    {
      type: "feature",
      title: "1분 회고",
      desc: "학습 핵심을 간단히 기록하여 학습 흐름을 유지하고, 이후 복습 시 참고 자료로 활용할 수 있습니다.",
      icon: faPenToSquare,
    },
    {
      type: "feature",
      title: "이해도 체크",
      desc: "1~100 점수로 이해도를 기록하여 학습 상태를 정량적으로 관리할 수 있습니다.",
      icon: faBullseye,
    },
    {
      type: "feature",
      title: "복습 추천",
      desc: "낮은 점수 및 반복 학습 주제를 기반으로 복습 필요 항목을 자동으로 정리해 제공합니다.",
      icon: faArrowsRotate,
    },
    {
      type: "feature",
      title: "성장 추적",
      desc: "주간·월간 학습 변화를 시각화하여 학습 성과 및 추세를 확인할 수 있습니다.",
      icon: faArrowTrendUp,
    },
  ];


  return (
  <>
    <section className="hero">
      <h1 className="hero-title">오늘의 학습을 기록으로 정리해 보세요.</h1>

      <div className="hero-showcase-wrap">
        <HeroShowcaseSlider slides={showcaseSlides} intervalMs={3200} />
      </div>

      
      <p className="hero-hint">회원가입 없이도 데모 기능을 먼저 확인하실 수 있습니다.</p>
    </section>

    <section className="demo section">
      <DemoTimeline />
    </section>

    {isLoggedIn && (
      <section className="dashboard section">
        <div className="dash-header">
          <h2 className="section-title">학습 대시보드</h2>
          <p className="section-desc">학습 기록과 이해도 추이를 한눈에 확인할 수 있습니다.</p>
        </div>

        <DashboardSlider intervalMs={5000} autoplay={true}>
          <WeeklyDonut />
          <TopicBar />
          <TrendLine />
        </DashboardSlider>
      </section>
    )}
  </>
);

}
