package com.githubProxy.mappers;

import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.gitHubClient.GitHubResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitHubClientMapper {
    RepositoryDto toDto(GitHubResponse response);
}
