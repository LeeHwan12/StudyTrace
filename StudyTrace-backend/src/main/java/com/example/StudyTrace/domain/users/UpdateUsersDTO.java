package com.example.StudyTrace.domain.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUsersDTO {

    @Size(min = 2, max = 20)
    private String nickname;   // null이면 변경 안 함

    @Email
    private String email;      // null이면 변경 안 함
}
