package com.example.StudyTrace.controller;

import com.example.StudyTrace.domain.users.PasswordChangeDTO;
import com.example.StudyTrace.domain.users.ResponseUserDTO;
import com.example.StudyTrace.domain.users.UpdateUsersDTO;
import com.example.StudyTrace.security.CustomUserDetails;
import com.example.StudyTrace.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<ResponseUserDTO> getMyPage(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                myPageService.getMyPage(user.getId())
        );
    }

    @PatchMapping
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid UpdateUsersDTO dto
    ) {
        myPageService.updateProfile(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid PasswordChangeDTO dto
    ) {
        myPageService.updatePassword(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/active")
    public ResponseEntity<Void> toggleActive(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        myPageService.changeActive(user.getId());
        return ResponseEntity.noContent().build();
    }
}
