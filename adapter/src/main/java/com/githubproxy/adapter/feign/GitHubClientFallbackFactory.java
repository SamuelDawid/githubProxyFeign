package com.githubproxy.adapter.feign;


import expections.GitHubClientException;
import expections.GitHubUnavailableException;
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
            public GitHubApiResponse getByOwnerAndRepositoryName(String owner, String repositoryName) {
                if(cause instanceof GitHubClientException gitHubClientException){
                    throw  gitHubClientException;
                }
                log.info("[Fallback] getByOwnerAndRepositoryName");
                throw new GitHubUnavailableException("GitHub Service Unavailable",cause);
            }
        };
    }
}
