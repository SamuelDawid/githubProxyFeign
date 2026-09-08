package com.githubProxy.exceptions.handler;

import com.githubProxy.dto.ErrorMessageDto;
import com.githubProxy.exceptions.RepositoryNotFoundException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GitHubClientExceptionHandler {
    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handleNotFound(RepositoryNotFoundException exception) {
        log.error("Repository not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDto(404,
                        exception.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(GitHubClientException.class)
    public ResponseEntity<ErrorMessageDto> handleGitHubClientException(GitHubClientException exception) {
        log.error("Rejected: {} -> {}", exception.getMessage(), exception.getStatus().value());
        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorMessageDto(exception.getStatus().value(),
                        exception.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorMessageDto> handleRetryableException(RetryableException exception) {
        log.error(" Rejected {} -> {}", exception.getMessage(), exception.status());
        return ResponseEntity.status(exception.status())
                .body(new ErrorMessageDto(
                        exception.status(),
                        "GitHub is temporarily unavailable, please try again later",
                        LocalDateTime.now()
                ));
    }
}
