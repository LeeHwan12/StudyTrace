import { useEffect, useMemo, useState } from "react";
import { Bar } from "react-chartjs-2";
import { apiFetch } from "../../lib/api"; // 네 fetch 래퍼

export default function TopicBar() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let alive = true;

    (async () => {
      try {
        setLoading(true);
        const data = await apiFetch("/api/stats/topics?days=30&limit=10");
        if (alive) setRows(Array.isArray(data) ? data : []);
      } catch (e) {
        if (alive) setRows([]);
      } finally {
        if (alive) setLoading(false);
      }
    })();

    return () => { alive = false; };
  }, []);

  const chart = useMemo(() => {
    const labels = rows.map((r) => `${r.topic} (n=${r.count})`);
    return {
      data: {
        labels,
        datasets: [
          {
            label: "이해도",
            data: rows.map((r) => r.avg),
            backgroundColor: "rgba(56, 189, 248, 0.55)",
            borderWidth: 0,
            borderRadius: 10,
          },
        ],
      },
      options: {
        indexAxis: "y",
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          x: { min: 0, max: 100, grid: { display: false } },
          y: { grid: { display: false } },
        },
      },
    };
  }, [rows]);

  return (
    <div className="card">
      <div className="card-title">복습이 필요한 주제</div>
      <div className="card-sub">점수가 낮은 순으로 보여줘요.</div>

      <div className="chart-box">
        {loading ? (
          <div className="empty">불러오는 중...</div>
        ) : rows.length === 0 ? (
          <div className="empty">표시할 데이터가 없어요.</div>
        ) : (
          <Bar data={chart.data} options={chart.options} />
        )}
      </div>
    </div>
  );
}
