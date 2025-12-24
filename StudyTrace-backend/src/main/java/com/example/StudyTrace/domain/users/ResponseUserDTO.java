package com.example.StudyTrace.domain.users;

import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.enums.Active;
import com.example.StudyTrace.enums.Roles;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseUserDTO {

    private Long id;
    private String nickname;
    private Roles role;
    private Active active;
    private LocalDateTime registerAt;
    private LocalDateTime lastLogin;

    public static ResponseUserDTO of(Users users) {
        return ResponseUserDTO.builder()
                .id(users.getId())
                .nickname(users.getNickname())
                .role(users.getRole())
                .active(users.getActive())
                .registerAt(users.getRegisterAt())
                .lastLogin(users.getLastLogin())
                .build();
    }
}
