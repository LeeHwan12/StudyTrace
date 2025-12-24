package com.example.StudyTrace.domain.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WeeklyTrendResponse {
    private final List<String> labels;  // ["W-3","W-2","W-1","This Week"]
    private final List<Integer> values; // [61,66,72,78]
    private final List<Long> counts;    // 주차별 표본 수 (optional)
}
