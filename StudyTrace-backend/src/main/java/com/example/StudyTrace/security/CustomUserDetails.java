package com.example.StudyTrace.security;

import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.enums.Active;
import com.example.StudyTrace.enums.Roles;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;

    private final String nickname;
    private final String email;
    private final LocalDateTime registerAt;
    private final LocalDateTime lastLogin;

    private final Roles role;
    private final Active active;

    private final List<GrantedAuthority> authorities;

    // ✅ Users 엔티티로부터 생성하는 방식 추천(실수 줄어듦)
    public CustomUserDetails(Users user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();

        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.registerAt = user.getRegisterAt();
        this.lastLogin = user.getLastLogin();

        this.role = user.getRole();
        this.active = user.getActive();

        this.authorities = List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // ✅ UserDetails 기본 계약 그대로
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    // ✅ 여기서 active로 로그인 허용/차단
    @Override
    public boolean isEnabled() {
        return this.active == Active.ACTIVE;
    }
}
