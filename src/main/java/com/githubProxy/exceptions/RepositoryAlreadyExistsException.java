package com.githubProxy.exceptions;

import com.githubProxy.exceptions.handler.GitHubClientException;
import org.springframework.http.HttpStatus;

public class RepositoryAlreadyExistsException extends GitHubClientException {
    public RepositoryAlreadyExistsException(String owner,String repositoryName) {

        super(owner + " already has repository " + repositoryName, HttpStatus.CONFLICT);
    }
}
