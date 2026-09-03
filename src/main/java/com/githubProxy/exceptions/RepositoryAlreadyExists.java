package com.githubProxy.exceptions;

import com.githubProxy.exceptions.handler.GitHubClientException;
import org.springframework.http.HttpStatus;

public class RepositoryAlreadyExists extends GitHubClientException {
    public RepositoryAlreadyExists(String owner,String repositoryName) {

        super(owner + " already has repository " + repositoryName, HttpStatus.CONFLICT);
    }
}
