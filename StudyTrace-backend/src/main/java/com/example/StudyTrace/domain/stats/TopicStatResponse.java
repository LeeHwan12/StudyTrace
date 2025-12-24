package com.example.StudyTrace.domain.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicStatResponse {

    private final String topic;
    private final int avg;
    private final Long count;

}
