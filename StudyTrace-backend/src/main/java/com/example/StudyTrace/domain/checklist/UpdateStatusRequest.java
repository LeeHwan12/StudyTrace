package com.example.StudyTrace.domain.checklist;

import com.example.StudyTrace.enums.CheckStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateStatusRequest {
    private CheckStatus status;
}
