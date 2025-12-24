package com.example.StudyTrace.domain.checklist;

import com.example.StudyTrace.enums.CheckItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CheckItemCreateRequest {
    @NotNull
    private CheckItemType type;

    @NotNull @Min(0) @Max(100)
    private Integer points;

    @NotBlank
    private String content;
}
