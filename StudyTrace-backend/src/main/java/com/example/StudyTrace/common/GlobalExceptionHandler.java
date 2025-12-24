package com.example.StudyTrace.common;

import com.example.StudyTrace.common.exception.BadRequestException;
import com.example.StudyTrace.common.exception.ConflictException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // ✅ 400: DTO 검증 실패(@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e,
                                                     HttpServletRequest req) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(req.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // ✅ 409: 중복/충돌(유저명/이메일/닉네임 중복 등)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException e,
                                                   HttpServletRequest req) {

        HttpStatus status = HttpStatus.CONFLICT;

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getMessage())
                .path(req.getRequestURI())
                .fieldErrors(null)
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // ✅ 400: 비즈니스 검증 실패(비번 확인 불일치 등)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException e,
                                                     HttpServletRequest req) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getMessage())
                .path(req.getRequestURI())
                .fieldErrors(null)
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // ✅ 401: 로그인 실패(비번 틀림 등)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException e,
            HttpServletRequest req
    ) {
        String message = messageSource.getMessage(
                "auth.login.failed",
                null,
                "아이디 또는 비밀번호가 올바르지 않습니다.",
                Locale.KOREA
        );

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(req.getRequestURI())
                .fieldErrors(null)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }


    // ✅ 403: 비활성 계정(active != ACTIVE) 등
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabled(DisabledException e,
                                                   HttpServletRequest req) {

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("Account is disabled")
                .path(req.getRequestURI())
                .fieldErrors(null)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // ✅ 401: 기타 인증 예외
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException e,
                                               HttpServletRequest req) {

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Authentication failed")
                .path(req.getRequestURI())
                .fieldErrors(null)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // ✅ 400: 그 외 잘못된 요청(최소 fallback)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e,
                                                          HttpServletRequest req) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getMessage())
                .path(req.getRequestURI())
                .fieldErrors(null)
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // ✅ 500: 나머지 예외(최후 방어)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e,
                                                    HttpServletRequest req) {

        ApiError body = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Internal server error")
                .path(req.getRequestURI())
                .fieldErrors(null)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
