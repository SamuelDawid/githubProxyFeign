package com.githubProxy.exceptions.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter

public class GitHubClientException extends RuntimeException {
    private final HttpStatus status;
    public GitHubClientException(String message,HttpStatus status) {
        super(message);
        this.status = status;
    }
}
