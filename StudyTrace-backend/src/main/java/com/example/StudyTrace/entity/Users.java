package com.example.StudyTrace.entity;

import com.example.StudyTrace.enums.Active;
import com.example.StudyTrace.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Roles role = Roles.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Active active = Active.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime registerAt;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

    @PrePersist
    public void prePersist(){
        this.registerAt = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.lastLogin = LocalDateTime.now();
    }

    public void updateLastLogin(){
        this.lastLogin = LocalDateTime.now();
    }

    public void updateProfile(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}

