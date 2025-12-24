package com.example.StudyTrace.controller;

import com.example.StudyTrace.domain.stats.WeeklyTrendResponse;
import com.example.StudyTrace.security.CustomUserDetails;
import com.example.StudyTrace.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class TrendController {

    private final TrendService trendService;

    @GetMapping("/trend/weekly")
    public WeeklyTrendResponse weeklyTrend(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "4") int weeks
    ) {
        return trendService.getWeeklyTrend(principal.getId(), weeks);
    }
}
