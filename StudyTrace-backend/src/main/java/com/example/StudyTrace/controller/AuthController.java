package com.example.StudyTrace.controller;

import com.example.StudyTrace.domain.users.LoginDTO;
import com.example.StudyTrace.domain.users.RegisterUserDTO;
import com.example.StudyTrace.domain.users.ResponseUserDTO;
import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.repository.UsersRepository;
import com.example.StudyTrace.security.CustomUserDetails;
import com.example.StudyTrace.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterService registerService;
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;

    @PostMapping("/register")
    public ResponseEntity<Void> registerAndAutoLogin(
            @RequestBody @Valid RegisterUserDTO dto,
            HttpServletRequest request
    ) {
        // 1) 회원가입
        String username = registerService.registerUser(dto);

        // 2) 바로 인증
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, dto.getPassword())
        );

        // 3) 세션에 SecurityContext 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // 4) (권장) lastLogin 갱신
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        Users user = usersRepository.findById(principal.getId()).orElseThrow();
        user.updateLastLogin();
        usersRepository.save(user);

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid LoginDTO dto,
            HttpServletRequest request
    ){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // lastLogin 갱신
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        Users user = usersRepository.findById(principal.getId()).orElseThrow();
        user.updateLastLogin();
        usersRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseUserDTO> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails principal
    ){
        if(principal == null){
            return ResponseEntity.status(401).build();
        }

        // ✅ DB 재조회 없이도 가능하지만(lastLogin 같은 최신값 필요하면 조회)
        Users user = usersRepository.findById(principal.getId()).orElseThrow();
        return ResponseEntity.ok(ResponseUserDTO.of(user));
    }
}
