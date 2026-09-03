package com.githubProxy.dto;

import java.time.OffsetDateTime;

public record RepositoryDto(
        String fullName,
        String description,
        String cloneUrl,
        Long stargazersCount,
        OffsetDateTime createdAt
) {
}
