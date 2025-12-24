package com.example.StudyTrace.enums;

public enum CheckStatus {
    NOT_DONE,      // 아직 안 함 (기본값)
    IN_PROGRESS,   // 진행 중
    DONE,          // 완료
    SKIPPED,       // 의도적으로 건너뜀(오늘은 제외)
    BLOCKED        // 못하는 이유가 있음(자료/시간/환경 문제)
}
