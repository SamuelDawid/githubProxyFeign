package com.githubProxy.exceptions;

import com.githubProxy.exceptions.handler.GitHubClientException;
import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends GitHubClientException {
    public RepositoryNotFoundException(String url) {
        super("Repository not found: " + url, HttpStatus.NOT_FOUND);
    }
}
