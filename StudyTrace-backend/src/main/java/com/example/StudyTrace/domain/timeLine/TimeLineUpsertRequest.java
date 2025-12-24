package com.example.StudyTrace.domain.timeLine;

import com.example.StudyTrace.enums.TimeLineCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeLineUpsertRequest {

    @NotBlank
    @Size(max = 100)
    private String topic;

    @NotBlank
    private String memo;

    @NotNull
    private TimeLineCategory category;
}
