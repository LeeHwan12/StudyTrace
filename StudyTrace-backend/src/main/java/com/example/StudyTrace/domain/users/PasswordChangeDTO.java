package com.example.StudyTrace.domain.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordChangeDTO {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 8, max = 30)
    private String newPassword;

    @NotBlank
    private String confirmNewPassword;
}
