package com.githubProxy.repositories;

import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.models.GitHubRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GitHubRepositoriesRepository extends JpaRepository<GitHubRepositoryEntity, Long> {
    List<RepositoryDto> findAllByOwner(String owner);

    RepositoryDto findByOwnerAndRepositoryName(String owner, String repositoryName);

    boolean existsByOwnerAndRepositoryName(String owner, String repositoryName);
}
