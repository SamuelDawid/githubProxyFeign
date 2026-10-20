package com.githubproxy.adapter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GitHubEntityJpaRepository extends JpaRepository<GitHubRepositoryEntity,Long> {
    Optional<GitHubRepositoryEntity> findByOwnerAndRepositoryName(String owner, String repositoryName);
    boolean existsByOwnerAndRepositoryName(String owner, String repositoryName);
}
