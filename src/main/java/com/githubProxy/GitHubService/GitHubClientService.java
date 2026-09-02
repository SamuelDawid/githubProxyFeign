package com.githubProxy.GitHubService;

import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
import com.githubProxy.gitHubClient.GitHubClient;
import com.githubProxy.gitHubClient.GitHubResponse;
import com.githubProxy.mappers.GitHubClientMapper;
import com.githubProxy.models.GitHubRepositoryEntity;
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
        log.info("Repository {} found with this details : {}",repositoryName,response);
        return gitHubClientMapper.toDto(response);
    }
    public GitHubRepositoryDto create(@NonNull String owner, @NonNull String repositoryName){
       log.info("Creating GitHubRepository entity for owner: {}, repository name: {}",owner,repositoryName);
        GitHubResponse response = gitHubClient.getByOwnerAndRepositoryName(owner, repositoryName);
        log.info("Found github repository {}",response);
        GitHubRepositoryEntity entity = gitHubClientMapper.toEntity(response,owner,repositoryName);
        log.info("Successfully Created Repository entity {}", entity);
        return gitHubClientMapper.toRepositoryDto(entity);
    }
}
