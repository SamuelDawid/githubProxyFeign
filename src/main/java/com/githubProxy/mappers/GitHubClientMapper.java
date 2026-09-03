package com.githubProxy.mappers;

import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
import com.githubProxy.gitHubClient.GitHubResponse;
import com.githubProxy.models.GitHubRepositoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GitHubClientMapper {
    RepositoryDto toDto(GitHubResponse response);

    GitHubRepositoryEntity toEntity(GitHubResponse response, String owner, String repositoryName);
    @Mapping(target = "stars", source = "stargazersCount")
    GitHubRepositoryDto toRepositoryDto(GitHubRepositoryEntity entity);
}
