export default function FeatureCards() {
  const features = [
    {
      title: "1분 회고",
      desc: "오늘 배운 내용을 짧게 정리해서 흐름을 놓치지 않아요.",
      icon: "✍️",
    },
    {
      title: "이해도 체크",
      desc: "1~100 점수로 오늘의 이해를 간단히 기록해요.",
      icon: "🎯",
    },
    {
      title: "복습 추천",
      desc: "점수가 낮았던 주제를 자동으로 모아줘요.",
      icon: "🔁",
    },
    {
      title: "성장 추적",
      desc: "주간/월간 변화가 한눈에 보이게 정리돼요.",
      icon: "📈",
    },
  ];

  return (
    <section className="features">
      <h2 className="section-title">What you can do</h2>
      <p className="section-desc">StudyTrace는 “기억”보다 “이해”를 남기게 도와줘요.</p>

      <div className="feature-grid">
        {features.map((f) => (
          <div className="feature-card" key={f.title}>
            <div className="feature-icon">{f.icon}</div>
            <div className="feature-title">{f.title}</div>
            <div className="feature-desc">{f.desc}</div>
          </div>
        ))}
      </div>
    </section>
  );
}
