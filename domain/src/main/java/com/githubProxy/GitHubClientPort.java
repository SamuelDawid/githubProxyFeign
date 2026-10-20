package com.githubProxy;

import model.GitHubResponse;

public interface GitHubClientPort {
    GitHubResponse getByOwnerAndRepositoryName(String owner,String repositoryName);
}
