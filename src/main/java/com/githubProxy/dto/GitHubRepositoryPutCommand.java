package com.githubProxy.dto;

import java.time.OffsetDateTime;

public record GitHubRepositoryPutCommand(
        String fullName,
        String description,
        String cloneUrl,
        Long stars,
        OffsetDateTime createdAt
) {
}
