package com.example.StudyTrace.domain.timeLine;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeeklyAvgResponse {

    LocalDate weekStart;
    LocalDate weekEnd;
    int avg;
    long count;
}
