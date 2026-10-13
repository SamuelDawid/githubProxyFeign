package com.githubProxy.gitHubClient;

import com.githubProxy.exceptions.GitHubUnavailableException;
import com.githubProxy.exceptions.handler.GitHubClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GitHubClientFallbackFactory implements FallbackFactory<GitHubClient> {

    @Override
    public GitHubClient create(Throwable cause) {
        log.error("An error occurred when calling the GitHubClient", cause);

        return new GitHubClient() {
            @Override
            public GitHubResponse getByOwnerAndRepositoryName(String owner, String repositoryName) {
                if(cause instanceof GitHubClientException gitHubClientException){
                    throw  gitHubClientException;
                }
                log.info("[Fallback] getByOwnerAndRepositoryName");
                throw new GitHubUnavailableException("GitHub Service Unavailable",cause);
            }
        };
    }
}
