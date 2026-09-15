package com.githubProxy.dto.gitHubRepositoryEntity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
@Schema(description = "Locally persisted repository record")
public record GitHubRepositoryDto(
        @Schema(example = "JohnShow/WinterfellRepository")
        String fullName,
        @Schema(example = "Plans on how to destroy Lannisters",nullable = true)
        String description,
        @Schema(format = "uri", example = "https://github.com/Winterfell/JhonShowPlannes.git")
        String cloneUrl,
        @Schema(description = "Star count")
        Long stars,
        OffsetDateTime createdAt
) {
}
