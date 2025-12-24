package com.example.StudyTrace.entity;

import com.example.StudyTrace.enums.TimeLineCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "timeline",
        indexes = {
                @Index(name = "idx_timeline_user_date", columnList = "user_id, study_date")
        }
)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TimeLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    @Column(nullable = false, length = 100)
    private String topic;

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private int score;

    @Lob
    @Column(nullable = false)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeLineCategory category;

    protected TimeLine() {}

    public void updateContent(String topic, String memo, TimeLineCategory category) {
        this.topic = topic;
        this.memo = memo;
        this.category = category;
    }

    public void forceUpdate(LocalDate studyDate, String topic, int score, String memo, TimeLineCategory category) {
        this.studyDate = studyDate;
        this.topic = topic;
        this.score = score;
        this.memo = memo;
        this.category = category;
    }

    public void changeScore(int score) {
        this.score = score;
    }
}
