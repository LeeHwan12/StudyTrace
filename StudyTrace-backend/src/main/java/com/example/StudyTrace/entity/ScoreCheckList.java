package com.example.StudyTrace.entity;

import com.example.StudyTrace.enums.CheckItemType;
import com.example.StudyTrace.enums.CheckStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "scoreCheckList")
public class ScoreCheckList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeLine_id" , nullable = false)
    private TimeLine timeLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CheckItemType type = CheckItemType.ETC;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CheckStatus status = CheckStatus.NOT_DONE;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private String content;
}
