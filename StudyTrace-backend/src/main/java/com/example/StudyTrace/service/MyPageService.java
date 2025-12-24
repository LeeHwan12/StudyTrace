package com.example.StudyTrace.service;

import com.example.StudyTrace.domain.users.PasswordChangeDTO;
import com.example.StudyTrace.domain.users.ResponseUserDTO;
import com.example.StudyTrace.domain.users.UpdateUsersDTO;
import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.enums.Active;
import com.example.StudyTrace.repository.UsersRepository;
import com.example.StudyTrace.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ResponseUserDTO getMyPage(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseUserDTO.of(user);
    }

    @Transactional
    public void updateProfile(Long userId, UpdateUsersDTO dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(dto.getNickname(), dto.getEmail());
    }

    @Transactional
    public void updatePassword(Long userId, PasswordChangeDTO dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1) 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 2) 새 비밀번호 확인
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 3) 기존 비밀번호 재사용 금지
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
        }

        // 4) 변경
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    @Transactional
    public void changeActive(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setActive(user.getActive().toggleByUser());
    }
}
