package com.githubproxy.adapter;


import model.RepositoryPOJO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GitHubClientMapper {
    RepositoryPOJO toPojo(GitHubRepositoryEntity entity);

    GitHubRepositoryEntity toEntityFromPojo(RepositoryPOJO repositoryPOJO);

}
