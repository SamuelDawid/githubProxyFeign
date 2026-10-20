package com.githubproxy.adapter;


import com.githubproxy.adapter.feign.GitHubApiResponse;
import com.githubproxy.adapter.feign.GitHubClient;
import lombok.RequiredArgsConstructor;
import com.githubProxy.GitHubClientPort;
import model.GitHubResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitHubClientAdapter implements GitHubClientPort {
    private final GitHubClient feignClient;

    @Override
    public GitHubResponse getByOwnerAndRepositoryName(String owner, String repositoryName) {
        GitHubApiResponse response = feignClient.getByOwnerAndRepositoryName(owner,repositoryName);
        return GitHubResponse.builder()
                .fullName(response.fullName())
                .description(response.description())
                .cloneUrl(response.cloneUrl())
                .stargazersCount(response.stargazersCount())
                .createdAt(response.createdAt())
                .build();
    }
}
