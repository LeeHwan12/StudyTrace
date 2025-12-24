import { Doughnut } from "react-chartjs-2";
import { useAuth } from "../../context/AuthContext";
import { useEffect, useMemo, useState } from "react";
import { apiFetch } from "../../lib/api";
import "./weeklyDonut.css";

export default function WeeklyDonut() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [avg, setAvg] = useState(0);      
  const [count, setCount] = useState(0); 
  const today = useMemo(() => new Date().toLocaleDateString("en-CA"), []);

  useEffect(() => {
    if (!user) return;

    const run = async () => {
      setLoading(true);
      setError("");
      try {
        const res = await apiFetch(`/api/stats/weekly-avg?date=${encodeURIComponent(today)}`);
        setAvg(typeof res?.avg === "number" ? res.avg : 0);
        setCount(typeof res?.count === "number" ? res.count : 0);  
      } catch (e) {
        setError(e?.message ?? "조회 실패");
        setAvg(0);
        setCount(0);
      } finally{
        setLoading(false);
      }
    }
    run();
  },[user, today]);

  const chartData = useMemo(() => {
    const safe = Math.max(0, Math.min(100, Number(avg) || 0));

    return {
      labels: ["Understood", "Need Review"],
      datasets: [
        {
          data: [safe, 100 - safe],
          backgroundColor: ["#38BDF8", "rgba(15, 23, 42, 0.10)"],
          borderWidth: 0,
        },
      ],
    };
  },[avg]);

  const options = useMemo(() => {
    return {
      cutout: "72%",
      plugins: { legend: { display: false }, tooltip: { enabled: false } },
      animation: { duration: 600 },
    };
  }, []);

  
  if (!user) {
    return (
      <div className="card">
        <div className="card-title">이번 주 평균 이해도</div>
        <div className="card-sub">로그인하면 이번 주 평균 이해도를 볼 수 있어요.</div>
      </div>
    );
  }

  return (
    <div className="weekly-donut">
      <div className="card">
        <div className="card-title">이번 주 평균 이해도</div>

        {loading && <div className="card-sub">불러오는 중…</div>}
        {error && !loading && <div className="card-sub error">{error}</div>}

        <div className="donut-wrap">
          <Doughnut data={chartData} options={options} />
          <div className="donut-center">
            <div className="donut-score">{avg}</div>
            <div className="donut-label">/ 100</div>
          </div>
        </div>

        <div className="card-sub">
          {count > 0 ? `이번 주 기록 ${count}일 기준` : "이번 주 기록이 아직 없어요."}
        </div>
      </div>
    </div>
  );
}
