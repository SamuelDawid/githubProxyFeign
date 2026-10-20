package com.githubproxy.adapter;

import com.githubProxy.GitHubRepositoryDto;
import com.githubProxy.GitHubRepositoryPutCommand;
import com.githubProxy.RepositoryDto;
import model.GitHubRepositoryInternalPutCommand;
import model.GitHubResponse;
import model.RepositoryPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GitHubWebMapper {
    RepositoryDto toDto(GitHubResponse response);

    @Mapping(target = "stars", source = "stargazersCount")
    GitHubRepositoryDto toDtoFromPojo(RepositoryPOJO repository);

    GitHubRepositoryInternalPutCommand toCommand(GitHubRepositoryPutCommand command);
}
