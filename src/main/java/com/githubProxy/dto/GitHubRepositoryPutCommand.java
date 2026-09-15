package com.githubProxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Payload for updating a local repository record")
public record GitHubRepositoryPutCommand(
        @Schema(example = "octocat/Hello-World")
        String fullName,
        @Schema(nullable = true)
        String description,
        @Schema(format = "uri", example = "https://github.com/octocat/Hello-World.git")
        String cloneUrl,
        @Schema(description = "Star count")
        Long stars,
        @Schema(description = "Repository creation timestamp")
        OffsetDateTime createdAt
) {
}
