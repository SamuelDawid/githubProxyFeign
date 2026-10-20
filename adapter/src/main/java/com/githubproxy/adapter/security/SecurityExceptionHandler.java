package com.githubproxy.adapter.security;
import com.githubProxy.ErrorMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {
    // 401 — brak tokena lub token nieważny
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessageDto> handleAuth(AuthenticationException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessageDto(401,
                        "Missing valid JWT token. Log in again.",
                        LocalDateTime.now()));
    }

    // 403 — token OK, ale brak uprawnień
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageDto> handleForbidden(AccessDeniedException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessageDto(403,
                        "Access denied.",
                        LocalDateTime.now()));
    }
}
