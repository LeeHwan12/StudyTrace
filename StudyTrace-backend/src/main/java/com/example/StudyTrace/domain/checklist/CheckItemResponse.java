package com.example.StudyTrace.domain.checklist;

import com.example.StudyTrace.enums.CheckItemType;
import com.example.StudyTrace.enums.CheckStatus;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CheckItemResponse {
    private Long id;
    private CheckStatus status;
    private CheckItemType type;
    private Integer points;
    private String content;
}
