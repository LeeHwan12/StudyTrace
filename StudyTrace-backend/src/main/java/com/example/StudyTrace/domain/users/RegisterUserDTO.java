package com.example.StudyTrace.domain.users;

import com.example.StudyTrace.entity.Users;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDTO {

    @NotBlank(message = "{users.username.notblank}")
    @Size(min = 4, max = 20, message = "{users.username.size}")
    private String username;

    @NotBlank(message = "{users.password.notblank}")
    @Size(min = 8, max = 30, message = "{users.password.size}")
    private String password;

    @NotBlank(message = "{users.passwordConfirm.notblank}")
    private String passwordConfirm;

    @Email(message = "{users.email.email}")
    private String email;

    @NotBlank(message = "{users.nickname.notblank}")
    @Size(min = 2, max = 20, message = "{users.nickname.size}")
    private String nickname;

    public Users toEntity(String encodedPassword) {
        return Users.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .nickname(nickname)
                .build();
    }
}

