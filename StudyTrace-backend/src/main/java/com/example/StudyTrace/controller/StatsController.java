package com.example.StudyTrace.controller;

import com.example.StudyTrace.domain.stats.TopicStatResponse;
import com.example.StudyTrace.domain.timeLine.WeeklyAvgResponse;
import com.example.StudyTrace.security.CustomUserDetails;
import com.example.StudyTrace.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/weekly-avg")
    public ResponseEntity<WeeklyAvgResponse>  getWeeklyAvg(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (user == null) return ResponseEntity.status(401).build();

        LocalDate baseDate = (date != null)
                ? date
                : LocalDate.now(ZoneId.of("Asia/Seoul"));
        WeeklyAvgResponse weeklyAvg = statsService.getWeeklyAvg(user.getId(),  baseDate);
        return ResponseEntity.ok(weeklyAvg);
    }

    @GetMapping("/topics")
    public List<TopicStatResponse> topicStats(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Long userId = principal.getId();
        return statsService.getTopicStats(userId, days, limit);
    }
}
