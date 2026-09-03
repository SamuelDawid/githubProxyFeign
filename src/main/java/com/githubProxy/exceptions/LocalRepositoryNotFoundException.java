package com.githubProxy.exceptions;

import com.githubProxy.exceptions.handler.GitHubClientException;
import org.springframework.http.HttpStatus;

public class LocalRepositoryNotFoundException extends GitHubClientException {
    public LocalRepositoryNotFoundException(String owner, String repositoryName) {
        super("Local repository " + repositoryName + " not found for: " + owner, HttpStatus.NOT_FOUND);
    }
}
