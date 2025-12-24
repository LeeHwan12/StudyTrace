import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { apiFetch } from "../../lib/api";
import "./timelineDetail.css";

const CHECK_STATUS = ["NOT_DONE", "IN_PROGRESS", "DONE", "SKIPPED", "BLOCKED"];
const CHECK_TYPE = [
  "READ",
  "WATCH",
  "PRACTICE",
  "SOLVE",
  "REVIEW",
  "NOTE",
  "PROJECT",
  "DEBUG",
  "TEST",
  "ETC",
];

// 보기 좋은 라벨(원하면 더 다듬자)
const STATUS_LABEL = {
  NOT_DONE: "아직 안 함",
  IN_PROGRESS: "진행 중",
  DONE: "완료",
  SKIPPED: "건너뜀",
  BLOCKED: "막힘",
};

const TYPE_LABEL = {
  READ: "읽기",
  WATCH: "영상",
  PRACTICE: "실습",
  SOLVE: "문제",
  REVIEW: "복습",
  NOTE: "정리",
  PROJECT: "프로젝트",
  DEBUG: "디버깅",
  TEST: "테스트",
  ETC: "기타",
};

function formatDate(v) {
  return v ? String(v) : "";
}

function statusToBool(status) {
  return String(status).toUpperCase() === "DONE";
}
function boolToStatus(checked) {
  return checked ? "DONE" : "NOT_DONE";
}

export default function TimelineDetail() {
  const { timelineId } = useParams();
  const { user } = useAuth();

  const [timeline, setTimeline] = useState(null);
  const [items, setItems] = useState([]);

  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  // add form (DTO 필수 3종)
  const [newType, setNewType] = useState("READ");
  const [newPoints, setNewPoints] = useState(10);
  const [newContent, setNewContent] = useState("");

  // edit modal (DTO 필수 3종 + status도 함께 편집 제공)
  const [editOpen, setEditOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null); // item 전체
  const [editType, setEditType] = useState("READ");
  const [editPoints, setEditPoints] = useState(10);
  const [editContent, setEditContent] = useState("");
  const [editStatus, setEditStatus] = useState("NOT_DONE");

  const canCallApi = !!user && !!timelineId;

  const title = timeline?.topic ?? "Timeline Detail";
  const dateText = formatDate(timeline?.studyDate);
  const memoText = timeline?.memo ?? "";

  const load = async () => {
    setLoading(true);
    setErr("");
    try {
      const [tl, list] = await Promise.all([
        apiFetch(`/api/timeline/${timelineId}`),
        apiFetch(`/api/check/${timelineId}`),
      ]);
      setTimeline(tl);
      setItems(Array.isArray(list) ? list : []);
    } catch (e) {
      setErr(`타임라인 조회 실패 (${e?.message ?? "unknown"})`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!canCallApi) return;
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [timelineId, user]);

  const addItem = async (e) => {
    e.preventDefault();
    const content = newContent.trim();
    if (!content) return;

    // points 유효 범위 보정(0~100)
    const points = Math.max(0, Math.min(100, Number(newPoints) || 0));

    try {
      const created = await apiFetch(`/api/check/${timelineId}`, {
        method: "POST",
        body: {
          type: newType,
          points,
          content,
        },
      });

      // apiFetch는 JSON을 리턴하므로 그대로 items에 push
      setItems((prev) => [...prev, created]);
      setNewContent("");
    } catch (e2) {
      alert(e2?.message ?? "추가 실패");
    }
  };

  const deleteItem = async (itemId) => {
    if (!window.confirm("삭제할까?")) return;
    try {
      await apiFetch(`/api/check/${itemId}`, { method: "DELETE" });
      setItems((prev) => prev.filter((x) => x.id !== itemId));
    } catch (e) {
      alert(e?.message ?? "삭제 실패");
    }
  };

  // 체크박스 토글 = DONE <-> NOT_DONE (단순 UX)
  const toggleDone = async (it) => {
    const nextChecked = !statusToBool(it.status);
    const nextStatus = boolToStatus(nextChecked);

    // optimistic update
    setItems((prev) =>
      prev.map((x) => (x.id === it.id ? { ...x, status: nextStatus } : x))
    );

    try {
      await apiFetch(`/api/check/${it.id}/status`, {
        method: "PATCH",
        body: { status: nextStatus },
      });
    } catch (e) {
      // rollback
      setItems((prev) =>
        prev.map((x) => (x.id === it.id ? { ...x, status: it.status } : x))
      );
      alert(e?.message ?? "상태 변경 실패");
    }
  };

  // 상태 드롭다운 변경 (5개 enum 풀 지원)
  const changeStatus = async (it, nextStatus) => {
    const prevStatus = it.status;

    setItems((prev) =>
      prev.map((x) => (x.id === it.id ? { ...x, status: nextStatus } : x))
    );

    try {
      await apiFetch(`/api/check/${it.id}/status`, {
        method: "PATCH",
        body: { status: nextStatus },
      });
    } catch (e) {
      setItems((prev) =>
        prev.map((x) => (x.id === it.id ? { ...x, status: prevStatus } : x))
      );
      alert(e?.message ?? "상태 변경 실패");
    }
  };

  const openEdit = (it) => {
    setEditTarget(it);
    setEditType(it.type ?? "READ");
    setEditPoints(typeof it.points === "number" ? it.points : 10);
    setEditContent(it.content ?? "");
    setEditStatus(it.status ?? "NOT_DONE");
    setEditOpen(true);
  };

  const closeEdit = () => {
    setEditOpen(false);
    setEditTarget(null);
  };

  const saveEdit = async (e) => {
    e.preventDefault();
    if (!editTarget) return;

    const content = editContent.trim();
    if (!content) return;

    const points = Math.max(0, Math.min(100, Number(editPoints) || 0));

    try {
      // 1) 내용(type/points/content) 수정
      await apiFetch(`/api/check/${editTarget.id}`, {
        method: "PATCH",
        body: {
          type: editType,
          points,
          content,
        },
      });

      // 2) status도 함께 바꾸고 싶으면 /status 호출
      //    (컨트롤러 분리 설계라면 이렇게 2번 호출하는 게 제일 명확함)
      if (String(editStatus) !== String(editTarget.status)) {
        await apiFetch(`/api/check/${editTarget.id}/status`, {
          method: "PATCH",
          body: { status: editStatus },
        });
      }

      // 서버가 Void 응답이어도 로컬 갱신으로 UI 반영
      setItems((prev) =>
        prev.map((x) =>
          x.id === editTarget.id
            ? { ...x, type: editType, points, content, status: editStatus }
            : x
        )
      );

      closeEdit();
    } catch (e2) {
      alert(e2?.message ?? "수정 실패");
    }
  };

  // 비로그인 가이드
  if (!user) {
    return (
      <div className="tl-wrap">
        <div className="tl-card">
          <h2 className="tl-title">타임라인 상세 보기</h2>
          <p className="tl-sub">
            로그인하면 타임라인 상세 내용과 체크리스트를 확인/관리할 수 있어.
          </p>
          <ul className="tl-guide">
            <li>오늘 공부한 주제 / 메모를 확인</li>
            <li>체크리스트로 학습 항목 관리</li>
            <li>상태/점수 기반으로 진행률 체크</li>
          </ul>
        </div>
      </div>
    );
  }

  return (
    <div className="tl-wrap">
      <div className="tl-head">
        <h1 className="tl-h1">{title}</h1>
        <p className="tl-meta">
          {dateText}
          <span className="tl-badge">Score {timeline?.score ?? 0}</span>
        </p>
      </div>

      {loading && <div className="tl-skeleton">불러오는 중…</div>}
      {err && !loading && <div className="tl-error">{err}</div>}

      {!loading && !err && (
        <>
          {/* 타임라인 본문 */}
          <section className="tl-card">
            <h2 className="tl-section">메모</h2>
            <p className="tl-memo">{memoText || "메모가 없습니다."}</p>
          </section>

          {/* 체크리스트 */}
          <section className="tl-card">
            <div className="tl-row">
              <h2 className="tl-section">체크리스트</h2>
              <span className="tl-count">{items.length} items</span>
            </div>

            {items.length === 0 ? (
              <div className="tl-empty">아직 항목이 없어. 아래에서 추가해봐!</div>
            ) : (
              <ul className="tl-list">
                {items.map((it) => (
                  <li key={it.id} className="tl-item">
                    {/* DONE 토글 */}
                    <label className="tl-check" title="완료 토글">
                      <input
                        type="checkbox"
                        checked={statusToBool(it.status)}
                        onChange={() => toggleDone(it)}
                      />
                      <span className="tl-check-ui" />
                    </label>

                    <div className="tl-item-body">
                      <div
                        className={
                          "tl-item-text " +
                          (statusToBool(it.status) ? "done" : "")
                        }
                      >
                        {it.content ?? "(no content)"}
                      </div>

                      <div className="tl-item-sub">
                        <span className="tl-pill">
                          {TYPE_LABEL[it.type] ?? String(it.type ?? "")}
                        </span>
                        <span className="tl-pill">{it.points ?? 0}점</span>

                        {/* 상태 드롭다운 */}
                        <select
                          className="tl-select"
                          value={it.status ?? "NOT_DONE"}
                          onChange={(e) => changeStatus(it, e.target.value)}
                        >
                          {CHECK_STATUS.map((s) => (
                            <option key={s} value={s}>
                              {STATUS_LABEL[s] ?? s}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>

                    <div className="tl-actions">
                      <button className="btn ghost" onClick={() => openEdit(it)}>
                        수정
                      </button>
                      <button
                        className="btn danger"
                        onClick={() => deleteItem(it.id)}
                      >
                        삭제
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}

            {/* 추가 폼 */}
            <form className="tl-add2" onSubmit={addItem}>
              <select
                className="tl-select"
                value={newType}
                onChange={(e) => setNewType(e.target.value)}
              >
                {CHECK_TYPE.map((t) => (
                  <option key={t} value={t}>
                    {TYPE_LABEL[t] ?? t}
                  </option>
                ))}
              </select>

              <input
                className="tl-input"
                type="number"
                min={0}
                max={100}
                value={newPoints}
                onChange={(e) => setNewPoints(e.target.value)}
                placeholder="points (0~100)"
              />

              <input
                className="tl-input"
                value={newContent}
                onChange={(e) => setNewContent(e.target.value)}
                placeholder="체크 항목 추가 (예: 알고리즘 문제 3개 풀기)"
              />

              <button className="btn primary" type="submit">
                추가
              </button>
            </form>
          </section>
        </>
      )}

      {/* 수정 모달 */}
      {editOpen && (
        <div className="modal-backdrop" onMouseDown={closeEdit}>
          <div className="modal" onMouseDown={(e) => e.stopPropagation()}>
            <div className="modal-head">
              <h3>체크 항목 수정</h3>
              <button className="icon-btn" onClick={closeEdit}>
                ✕
              </button>
            </div>

            <form onSubmit={saveEdit} className="modal-body">
              <select
                className="tl-select"
                value={editType}
                onChange={(e) => setEditType(e.target.value)}
              >
                {CHECK_TYPE.map((t) => (
                  <option key={t} value={t}>
                    {TYPE_LABEL[t] ?? t}
                  </option>
                ))}
              </select>

              <input
                className="tl-input"
                type="number"
                min={0}
                max={100}
                value={editPoints}
                onChange={(e) => setEditPoints(e.target.value)}
                placeholder="points (0~100)"
              />

              <input
                className="tl-input"
                value={editContent}
                onChange={(e) => setEditContent(e.target.value)}
                placeholder="내용 수정"
                autoFocus
              />

              <select
                className="tl-select"
                value={editStatus}
                onChange={(e) => setEditStatus(e.target.value)}
              >
                {CHECK_STATUS.map((s) => (
                  <option key={s} value={s}>
                    {STATUS_LABEL[s] ?? s}
                  </option>
                ))}
              </select>

              <div className="modal-actions">
                <button type="button" className="btn ghost" onClick={closeEdit}>
                  취소
                </button>
                <button type="submit" className="btn primary">
                  저장
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
