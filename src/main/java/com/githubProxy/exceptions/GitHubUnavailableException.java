package com.githubProxy.exceptions;

import com.githubProxy.exceptions.handler.GitHubClientException;
import org.springframework.http.HttpStatus;

import java.awt.event.FocusEvent;

public class GitHubUnavailableException extends GitHubClientException {
    private final Throwable cause;
    public GitHubUnavailableException(String message, Throwable cause) {
        super(message,HttpStatus.SERVICE_UNAVAILABLE);
        this.cause = cause;
    }
}
