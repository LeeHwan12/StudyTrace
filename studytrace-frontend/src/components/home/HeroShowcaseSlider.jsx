import { useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faChevronRight, faPause, faPlay } from "@fortawesome/free-solid-svg-icons";

export default function HeroShowcaseSlider({ slides = [], intervalMs = 3200 }) {
  const list = useMemo(() => slides.filter(Boolean), [slides]);
  const [idx, setIdx] = useState(0);
  const [paused, setPaused] = useState(false);

  // ✅ absolute 슬라이드 잘림 해결: 현재 슬라이드 높이를 측정해서 viewport 높이로 반영
  const slideRefs = useRef([]);
  const [viewportH, setViewportH] = useState(null);

  const hasSlides = list.length > 0;

  const next = () => {
    if (!hasSlides) return;
    setIdx((p) => (p + 1) % list.length);
  };

  const prev = () => {
    if (!hasSlides) return;
    setIdx((p) => (p - 1 + list.length) % list.length);
  };

  // autoplay
  useEffect(() => {
    if (!hasSlides || paused) return;
    const t = setInterval(next, intervalMs);
    return () => clearInterval(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [hasSlides, paused, intervalMs]);

  // slides 변경 시 idx 보정
  useEffect(() => {
    if (!hasSlides) return;
    if (idx >= list.length) setIdx(0);
  }, [hasSlides, idx, list.length]);

  const measure = () => {
    const el = slideRefs.current[idx];
    if (!el) return;
    const h = el.offsetHeight;
    if (h && h !== viewportH) setViewportH(h);
  };

  // ✅ 렌더 직후 높이 측정
  useLayoutEffect(() => {
    if (!hasSlides) return;
    measure();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idx, hasSlides, list.length]);

  // ✅ 리사이즈 시 높이 다시 측정
  useEffect(() => {
    if (!hasSlides) return;
    const onResize = () => measure();
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idx, hasSlides]);

  const onKeyDown = (e) => {
    if (e.key === "ArrowLeft") prev();
    if (e.key === "ArrowRight") next();
    if (e.key === " " || e.key === "Enter") {
      // 스페이스/엔터로 재생/일시정지
      e.preventDefault();
      setPaused((p) => !p);
    }
  };

  if (!hasSlides) return null;

  const total = list.length;
  const pageText = `${String(idx + 1).padStart(2, "0")} / ${String(total).padStart(2, "0")}`;

  return (
    <div
      className="hero-showcase"
      tabIndex={0}
      role="region"
      aria-label="히어로 기능 안내"
      onKeyDown={onKeyDown}
      onMouseEnter={() => setPaused(true)}
      onMouseLeave={() => setPaused(false)}
      onFocus={() => setPaused(true)}
      onBlur={() => setPaused(false)}
    >
      <div
        className="hero-showcase-viewport"
        style={viewportH ? { height: viewportH } : undefined}
      >
        {list.map((s, i) => (
          <div
            key={i}
            ref={(el) => (slideRefs.current[i] = el)}
            className={`hero-showcase-slide ${i === idx ? "is-active" : ""}`}
            aria-hidden={i === idx ? "false" : "true"}
          >
            {s.type === "text" ? (
              <p className="hero-subtitle">{s.text}</p>
            ) : (
              <div className="hero-feature-card is-column">
                
                <div className="hero-feature-title">{s.title}</div>
                <div className="hero-feature-box">
                  <div className="hero-feature-icon" aria-hidden="true">
                    <FontAwesomeIcon icon={s.icon} />
                  </div>
                  <div className="hero-feature-desc">{s.desc}</div>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      {/* ✅ 학원 느낌 컨트롤: 좌측 정렬 + 페이지(01/04) + 재생/일시정지 */}
      <div className="hero-controls">
        <div className="hero-controls-left">
          <span className="hero-page" aria-label="슬라이드 진행">
            {pageText}
          </span>

          <div className="hero-dots" aria-label="슬라이드 선택">
            {list.map((_, i) => (
              <button
                key={i}
                type="button"
                className={`hero-dot ${i === idx ? "is-active" : ""}`}
                onClick={() => setIdx(i)}
                aria-label={`${i + 1}번째 슬라이드`}
                aria-current={i === idx ? "true" : "false"}
              />
            ))}
          </div>
        </div>

        <div className="hero-controls-right">
          <button type="button" className="hero-ctrl-btn" onClick={prev} aria-label="이전">
            <FontAwesomeIcon icon={faChevronLeft} />
          </button>
          <button type="button" className="hero-ctrl-btn" onClick={next} aria-label="다음">
            <FontAwesomeIcon icon={faChevronRight} />
          </button>
        </div>
      </div>
    </div>
  );
}
