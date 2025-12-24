package com.example.StudyTrace.controller;

import com.example.StudyTrace.domain.timeLine.ResponseTimeLineDTO;
import com.example.StudyTrace.domain.timeLine.TimeLineUpsertRequest;
import com.example.StudyTrace.enums.TimeLineCategory;
import com.example.StudyTrace.security.CustomUserDetails;
import com.example.StudyTrace.service.TimeLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimeLineController {

    private final TimeLineService timeLineService;

    @PostMapping
    public ResponseEntity<Void> createTimeLine(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid TimeLineUpsertRequest timeLine
    ) {
        timeLineService.createTimeLine(user.getId(), timeLine);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{timelineId}")
    public ResponseEntity<Void> updateTimeLine(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid TimeLineUpsertRequest timeLine,
            @PathVariable Long timelineId
    ) {
        timeLineService.updateTimeLine(user.getId(), timelineId, timeLine);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{timelineId}")
    public ResponseEntity<Void> deleteTimeLine(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long timelineId
    ) {
        timeLineService.deleteTimeLine(user.getId(), timelineId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ResponseTimeLineDTO>> getTimeLines(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate studyDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)TimeLineCategory category
            ) {
        return ResponseEntity.ok(
                timeLineService.getTimeLines(user.getId(), studyDate, keyword, category)
        );
    }

    @GetMapping("/{timelineId}")
    public ResponseEntity<ResponseTimeLineDTO> getTimeLine(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long timelineId
    ){
        ResponseTimeLineDTO dto = timeLineService.getTimeLine(user.getId(), timelineId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/scroll")
    public ResponseEntity<com.example.StudyTrace.domain.timeLine.TimeLineSliceResponse> getTimeLinesScroll(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TimeLineCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                timeLineService.getTimeLinesSlice(user.getId(), studyDate, keyword, category, page, size)
        );
    }
}
