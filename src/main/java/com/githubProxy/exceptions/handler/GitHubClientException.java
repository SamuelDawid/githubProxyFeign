package com.githubProxy.exceptions.handler;

import org.springframework.http.HttpStatus;

public class GitHubClientException extends RuntimeException {
    private HttpStatus status;
    public GitHubClientException(String message,HttpStatus status) {

        super(message);
        this.status = status;
    }
}
