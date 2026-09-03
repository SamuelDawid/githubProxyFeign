package com.githubProxy.repositories;

import com.githubProxy.models.GitHubRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GitHubRepositoriesRepository extends JpaRepository<GitHubRepositoryEntity, Long> {
    Optional<GitHubRepositoryEntity> findByOwnerAndRepositoryName(String owner, String repositoryName);

    boolean existsByOwnerAndRepositoryName(String owner, String repositoryName);
}
