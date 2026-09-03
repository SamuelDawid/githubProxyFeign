package com.githubProxy.dto.gitHubRepositoryEntity;

import java.time.OffsetDateTime;

public record GitHubRepositoryDto(
        String fullName,
        String description,
        String cloneUrl,
        Long stars,
        OffsetDateTime createdAt
) {
}
