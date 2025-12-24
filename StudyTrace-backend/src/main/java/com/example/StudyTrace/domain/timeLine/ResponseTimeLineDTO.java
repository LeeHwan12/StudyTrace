package com.example.StudyTrace.domain.timeLine;

import com.example.StudyTrace.entity.TimeLine;
import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.enums.TimeLineCategory;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTimeLineDTO {

    private Long id;
    private TimeLineUserDTO user;
    private LocalDate studyDate;
    private String topic;
    private int score;
    private String memo;
    private TimeLineCategory category;
    public static ResponseTimeLineDTO of(TimeLine timeLine) {
        return ResponseTimeLineDTO.builder()
                .id(timeLine.getId())
                .user(new TimeLineUserDTO(
                        timeLine.getUser().getId(),
                        timeLine.getUser().getNickname()
                ))
                .category(timeLine.getCategory())
                .studyDate(timeLine.getStudyDate())
                .topic(timeLine.getTopic())
                .score(timeLine.getScore())
                .memo(timeLine.getMemo())
                .build();
    }
}

