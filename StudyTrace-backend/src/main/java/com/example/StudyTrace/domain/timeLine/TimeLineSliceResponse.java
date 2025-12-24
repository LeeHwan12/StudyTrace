package com.example.StudyTrace.domain.timeLine;

import java.util.List;

public record TimeLineSliceResponse(
        List<ResponseTimeLineDTO> items,
        boolean hasNext,
        int nextPage
) {}
