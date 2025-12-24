package com.example.StudyTrace.controller;

import com.example.StudyTrace.domain.checklist.CheckItemCreateRequest;
import com.example.StudyTrace.domain.checklist.CheckItemResponse;
import com.example.StudyTrace.domain.checklist.UpdateStatusRequest;
import com.example.StudyTrace.security.CustomUserDetails;
import com.example.StudyTrace.service.CheckListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check")
public class ScoreCheckListController {

    private final CheckListService checkListService;

    @GetMapping("/{timelineId}")
    public ResponseEntity<List<CheckItemResponse>> getScoreCheckList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long timelineId
    ) {
        List<CheckItemResponse> checkList = checkListService.getItems(user.getId(), timelineId);
        return ResponseEntity.ok(checkList);
    }

    @PostMapping("/{timelineId}")
    public ResponseEntity<CheckItemResponse> createScoreCheckList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long timelineId,
            @RequestBody CheckItemCreateRequest request
    ) {
        CheckItemResponse created = checkListService.addItem(user.getId(), timelineId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/items/{itemId}")
                .buildAndExpand(created.getId())   // created에 id가 있어야 함
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Void> updateScoreCheckList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long itemId,
            @RequestBody CheckItemCreateRequest request
    ) {
        checkListService.updateItem(user.getId(), itemId, request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{itemId}/status")
    public ResponseEntity<Void> updateScoreCheckListStatus(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long itemId,
            @RequestBody UpdateStatusRequest request
    ){
        checkListService.updateStatus(user.getId(), itemId, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteScoreCheckList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long itemId
    ) {
        checkListService.deleteItem(user.getId(), itemId);
        return ResponseEntity.noContent().build();
    }
}
