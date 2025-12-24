package com.example.StudyTrace.service;

import com.example.StudyTrace.domain.stats.TopicStatResponse;
import com.example.StudyTrace.domain.timeLine.WeeklyAvgResponse;
import com.example.StudyTrace.enums.TimeLineCategory;
import com.example.StudyTrace.repository.TimeLineRepository;
import com.example.StudyTrace.repository.projection.TopicStatProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final TimeLineRepository timeLineRepository;


    public WeeklyAvgResponse getWeeklyAvg(Long userId, LocalDate baseDate) {
        LocalDate weekStart = baseDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd   = baseDate.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        double avgRaw = timeLineRepository.avgScoreByUserAndDateBetween(userId, weekStart, weekEnd);

        long count = timeLineRepository.countByUser_IdAndStudyDateBetween(userId, weekStart, weekEnd);
        int avg = (count == 0) ? 0 : (int) Math.round(avgRaw);


        return WeeklyAvgResponse.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .avg(avg)
                .count(count)

                .build();

    }

    @Transactional(readOnly = true)
    public List<TopicStatResponse> getTopicStats(Long userId, int days, int limit) {
        int safeDays = Math.max(1, Math.min(days, 365));
        int safeLimit = Math.max(1, Math.min(limit, 30));

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(safeDays - 1);

        List<TopicStatProjection> rows = timeLineRepository.findTopicStats(userId, from, to);

        return rows.stream()
                .limit(safeLimit)
                .map(p -> new TopicStatResponse(
                        p.getTopic(),
                        (int) Math.round(p.getAvgScore() == null ? 0.0 : p.getAvgScore()),
                        p.getCnt() == null ? 0L : p.getCnt()
                ))
                .toList();
    }
}
