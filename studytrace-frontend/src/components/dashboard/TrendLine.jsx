import { useEffect, useMemo, useState } from "react";
import { Line } from "react-chartjs-2";
import { apiFetch } from "../../lib/api"

export default function TrendLine() {
  const [labels, setLabels] = useState(["W-3", "W-2", "W-1", "This Week"]);
  const [values, setValues] = useState([0, 0, 0, 0]);

  useEffect(() => {
    (async () => {
      try {
        const res = await apiFetch("/api/stats/trend/weekly?weeks=4");
        setLabels(res.labels ?? []);
        setValues(res.values ?? []);
      } catch (e) {
        // 실패 시 기존 값 유지 or 0 처리
      }
    })();
  }, []);

  const data = useMemo(() => ({
    labels,
    datasets: [
      {
        label: "주간 평균 이해도",
        data: values,
        tension: 0.35,
        borderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 5,
        borderColor: "#38BDF8",
        backgroundColor: "rgba(56, 189, 248, 0.15)",
        fill: true,
      },
    ],
  }), [labels, values]);

  const options = {
    plugins: { legend: { display: false } },
    scales: {
      y: { min: 0, max: 100, grid: { display: false } },
      x: { grid: { display: false } },
    },
  };

  return (
    <div className="card">
      <div className="card-title">최근 4주 성장 추세</div>
      <div className="card-sub">꾸준함이 숫자로 보이기 시작해요.</div>
      <div className="chart-box">
        <Line data={data} options={options} />
      </div>
    </div>
  );
}
