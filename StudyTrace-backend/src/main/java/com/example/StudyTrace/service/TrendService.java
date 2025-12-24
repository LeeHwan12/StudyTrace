package com.example.StudyTrace.service;

import com.example.StudyTrace.domain.stats.WeeklyTrendResponse;
import com.example.StudyTrace.entity.TimeLine;
import com.example.StudyTrace.repository.TimeLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final TimeLineRepository timeLineRepository;

    @Transactional(readOnly = true)
    public WeeklyTrendResponse getWeeklyTrend(Long userId, int weeks) {
        int n = Math.max(1, Math.min(weeks, 12)); // 최대 12주 정도로 제한

        LocalDate today = LocalDate.now();
        LocalDate thisWeekStart = today.with(DayOfWeek.MONDAY);
        LocalDate from = thisWeekStart.minusWeeks(n - 1);
        LocalDate to = thisWeekStart.plusDays(6);

        List<TimeLine> items = timeLineRepository
                .findByUser_IdAndStudyDateBetweenOrderByStudyDateAsc(userId, from, to);

        // weekStart(Monday) -> [sum, count]
        Map<LocalDate, long[]> agg = new HashMap<>();
        for (TimeLine tl : items) {
            LocalDate weekStart = tl.getStudyDate().with(DayOfWeek.MONDAY);
            long[] sc = agg.computeIfAbsent(weekStart, k -> new long[]{0L, 0L});
            sc[0] += tl.getScore();
            sc[1] += 1;
        }

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (int i = n - 1; i >= 0; i--) {
            LocalDate weekStart = thisWeekStart.minusWeeks(i);

            String label = (i == 0) ? "This Week" : "W-" + i;
            labels.add(label);

            long[] sc = agg.getOrDefault(weekStart, new long[]{0L, 0L});
            long sum = sc[0];
            long cnt = sc[1];

            // ✅ 빈 주 처리 정책 1) 0점으로 처리
            int avg = (cnt == 0) ? 0 : (int) Math.round((double) sum / cnt);

            values.add(avg);
            counts.add(cnt);
        }

        return new WeeklyTrendResponse(labels, values, counts);
    }
}
