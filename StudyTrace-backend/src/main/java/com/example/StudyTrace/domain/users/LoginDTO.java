package com.example.StudyTrace.domain.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "{login.username.notblank}")
    private String username;

    @NotBlank(message = "{login.password.notblank}")
    private String password;
}
