package com.githubproxy.adapter;


import com.githubProxy.GitHubRepositoryDto;
import com.githubProxy.RepositoryDto;
import model.GitHubResponse;
import model.RepositoryPOJO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GitHubClientMapper {
    RepositoryDto toDto(GitHubResponse response);

    GitHubRepositoryEntity toEntity(GitHubResponse response, String owner, String repositoryName);

    RepositoryPOJO toPojo(GitHubRepositoryEntity entity);

    GitHubRepositoryEntity toEntityFromPojo(RepositoryPOJO repositoryPOJO);

    @Mapping(target = "stars", source = "stargazersCount")
    GitHubRepositoryDto toRepositoryDto(GitHubRepositoryEntity entity);
}
