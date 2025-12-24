import { useEffect, useMemo, useRef, useState } from "react";

export default function DashboardSlider({
  children,
  intervalMs = 4500,
  autoplay = true,
}) {
  const slides = useMemo(() => {
    const arr = Array.isArray(children) ? children : [children];
    return arr.filter(Boolean);
  }, [children]);

  const [idx, setIdx] = useState(0);
  const [paused, setPaused] = useState(false);
  const timerRef = useRef(null);

  const has = slides.length > 0;

  const next = () => has && setIdx((p) => (p + 1) % slides.length);
  const prev = () => has && setIdx((p) => (p - 1 + slides.length) % slides.length);

  useEffect(() => {
    if (!has || !autoplay || paused) return;
    timerRef.current = setInterval(next, intervalMs);
    return () => timerRef.current && clearInterval(timerRef.current);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [has, autoplay, paused, intervalMs]);

  useEffect(() => {
    if (!has) return;
    if (idx >= slides.length) setIdx(0);
  }, [has, idx, slides.length]);

  const onKeyDown = (e) => {
    if (e.key === "ArrowLeft") prev();
    if (e.key === "ArrowRight") next();
  };

  if (!has) return null;

  return (
    <div
      className="dash-slider"
      tabIndex={0}
      role="region"
      aria-label="대시보드 슬라이더"
      onKeyDown={onKeyDown}
      onMouseEnter={() => setPaused(true)}
      onMouseLeave={() => setPaused(false)}
      onFocus={() => setPaused(true)}
      onBlur={() => setPaused(false)}
    >
      <div className="dash-slider-viewport">
        <div
          className="dash-slider-track"
          style={{ transform: `translateX(-${idx * 100}%)` }}
        >
          {slides.map((node, i) => (
            <div className="dash-slide" key={i}>
              {node}
            </div>
          ))}
        </div>
      </div>

      <div className="dash-slider-controls">
        <button type="button" className="dash-nav" onClick={prev} aria-label="이전">
          ‹
        </button>

        <div className="dash-dots" aria-label="슬라이드 선택">
          {slides.map((_, i) => (
            <button
              key={i}
              type="button"
              className={`dash-dot ${i === idx ? "is-active" : ""}`}
              onClick={() => setIdx(i)}
              aria-label={`${i + 1}번째 대시보드`}
              aria-current={i === idx ? "true" : "false"}
            />
          ))}
        </div>

        <button type="button" className="dash-nav" onClick={next} aria-label="다음">
          ›
        </button>
      </div>
    </div>
  );
}
