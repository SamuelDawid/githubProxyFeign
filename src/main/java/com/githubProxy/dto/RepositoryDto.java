package com.githubProxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
@Schema(description = "Live repository details as returned directly from GitHub")
public record RepositoryDto(
        @Schema(example = "owner/RepositoryName")
        String fullName,
        @Schema(nullable = true)
        String description,
        @Schema(format = "uri",example = "https://github.com/octocat/Hello-World.git")
        String cloneUrl,
        @Schema(description = "Number of stars",example = "1")
        Long stargazersCount,
        @Schema(description = "Repository creation timestamp")
        OffsetDateTime createdAt
) {
}
