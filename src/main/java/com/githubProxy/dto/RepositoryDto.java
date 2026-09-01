package com.githubProxy.dto;

import java.time.LocalDateTime;

public record RepositoryDto(
        String fullName,
        String description,
        String cloneUrl,
        Long stargazersCount,
        LocalDateTime createdAt
) {
}
