package com.githubProxy.GitHubService;

import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.gitHubClient.GitHubClient;
import com.githubProxy.gitHubClient.GitHubResponse;
import com.githubProxy.mappers.GitHubClientMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubClientService {
    private final GitHubClient gitHubClient;
    private final GitHubClientMapper gitHubClientMapper;

    public RepositoryDto getByOwnerAndRepositoryName(@NonNull String owner, @NonNull String repositoryName) {
        log.info("Finding repository with name {} by {}", repositoryName, owner);
        GitHubResponse response = gitHubClient.getByOwnerAndRepositoryName(owner, repositoryName);
        return gitHubClientMapper.toDto(response);
    }
}
