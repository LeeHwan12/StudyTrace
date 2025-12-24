import { useEffect, useMemo, useRef, useState, useCallback } from "react";
import { apiFetch } from "../../lib/api";
import { useAuth } from "../../context/AuthContext";
import "../home/demoTimeline.css";
import { Link } from "react-router-dom";

export default function DemoTimeline() {
  const { user } = useAuth();

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [studyDate, setStudyDate] = useState("");
  const [keyword, setKeyword] = useState("");

  // ✅ 무한스크롤 상태
  const [page, setPage] = useState(0);
  const [hasNext, setHasNext] = useState(true);

  // ✅ 타임라인 “영역 내부 스크롤”용 ref
  const scrollBoxRef = useRef(null);
  const sentinelRef = useRef(null);

  const demoItems = [
    { studyDate: "2025-12-21", topic: "네트워크 - TCP 3-way handshake", score: 74, memo: "..." },
    { studyDate: "2025-12-20", topic: "자료구조 - HashMap", score: 82, memo: "..." },
    { studyDate: "2025-12-19", topic: "Spring - @Transactional", score: 67, memo: "..." },
  ];

  const baseQuery = useMemo(() => {
    const params = new URLSearchParams();
    if (studyDate) params.set("studyDate", studyDate);
    if (keyword.trim()) params.set("keyword", keyword.trim());
    return params;
  }, [studyDate, keyword]);

  // ✅ page 단위로 추가 로딩
  const loadMore = useCallback(async () => {
    if (!user) return;
    if (loading || !hasNext) return;

    setLoading(true);
    setError("");

    try {
      const params = new URLSearchParams(baseQuery);
      params.set("page", String(page));
      params.set("size", "10");

      const res = await apiFetch(`/api/timeline/scroll?${params.toString()}`, { method: "GET" });

      const newItems = res.items ?? [];
      setItems((prev) => [...prev, ...newItems]);

      setHasNext(!!res.hasNext);
      setPage(res.nextPage ?? page + 1);
    } catch (e) {
      setError("타임라인을 불러오는 데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  }, [user, loading, hasNext, page, baseQuery]);

  // ✅ 필터/로그인 상태 바뀌면: 리스트 리셋 후 0페이지부터 다시
  useEffect(() => {
    if (!user) return;

    setItems([]);
    setPage(0);
    setHasNext(true);
    setError("");

    // 스크롤도 맨 위로
    if (scrollBoxRef.current) scrollBoxRef.current.scrollTop = 0;
  }, [user, baseQuery]);

  // ✅ 리셋 직후 첫 로딩
  useEffect(() => {
    if (!user) return;
    if (page !== 0) return; // 리셋 후 첫 페이지만 여기서
    loadMore();
  }, [user, page, loadMore]);

  // ✅ “타임라인 박스 안에서만” IntersectionObserver 동작
  useEffect(() => {
    const rootEl = scrollBoxRef.current;
    const target = sentinelRef.current;
    if (!user || !rootEl || !target) return;

    const io = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) loadMore();
      },
      {
        root: rootEl,        // ✅ window가 아니라 타임라인 박스
        rootMargin: "160px",
        threshold: 0.01,
      }
    );

    io.observe(target);
    return () => io.disconnect();
  }, [user, loadMore]);

  const visibleItems = user ? items : demoItems;

  return (
    <section className="demo">
      <div className="demo-head">
        <div>
          <h2 className="section-title">Timeline</h2>
          <p className="section-desc">
            {user
              ? "학습 기록을 등록하고 관리할 수 있습니다."
              : "로그인 후 개인 학습 타임라인을 이용하실 수 있습니다."}
          </p>
        </div>
      </div>

      {user && (
        <div className="filters">
          <input type="date" value={studyDate} onChange={(e) => setStudyDate(e.target.value)} className="filter-input" />
          <input type="text" value={keyword} onChange={(e) => setKeyword(e.target.value)} className="filter-input" placeholder="주제 검색" />
          <button className="filter-btn" onClick={() => { setStudyDate(""); setKeyword(""); }}>
            초기화
          </button>
        </div>
      )}

      {/* ✅ 여기(.timeline)가 “내부 스크롤 + 무한 스크롤 영역” */}
      <div className="timeline timeline-scroll" ref={scrollBoxRef}>
        {visibleItems.map((it, idx) => (
          <Link to={`/timeline/${it.id}`} className="timeline-link" key={it.id ?? idx}>
            <div className="timeline-item">
              <div className="timeline-meta">
                <span className="pill">{it.studyDate}</span>
                <span className="pill category">{it.category ?? "ETC"}</span>
                <span className="score">{it.score} / 100</span>
              </div>
              <div className="timeline-topic">{it.topic}</div>
              <div className="timeline-memo">{it.memo}</div>
            </div>
          </Link>
        ))}

        {/* ✅ 바닥 감지용 */}
        {user && <div ref={sentinelRef} />}

        {user && loading && <div className="timeline-empty">불러오는 중입니다.</div>}
        {user && !loading && error && <div className="timeline-empty">{error}</div>}
        {user && !loading && !error && items.length === 0 && (
          <div className="timeline-empty">등록된 학습 기록이 없습니다.</div>
        )}
        {user && !loading && !error && items.length > 0 && !hasNext && (
          <div className="timeline-empty">마지막 기록입니다.</div>
        )}
      </div>
    </section>
  );
}
