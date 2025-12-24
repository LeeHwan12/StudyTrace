package com.example.StudyTrace.repository.projection;

public interface TopicStatProjection {
    String getTopic();
    Double getAvgScore();
    Long getCnt();
}
