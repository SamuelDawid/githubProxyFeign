package com.githubProxy.dto.gitHubRepositoryEntity;

import java.time.OffsetDateTime;

public record GitHubRepositoryDto(
        Long id,
        String owner,
        String repositoryName,
        String fullName,
        String description,
        String cloneUrl,
        Long stargazersCount,
        OffsetDateTime createdAt
) {
}
