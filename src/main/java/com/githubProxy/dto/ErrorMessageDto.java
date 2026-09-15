package com.githubProxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
@Schema(description = "Standard error response body")
public record ErrorMessageDto(
        @Schema(example = "404")
        Integer status,
        @Schema(example = "Repository 'Hello-World' not found for owner OwnerName")
        String message,
        @Schema(description = "Timestamp of the occurred error")
        LocalDateTime occurredAt
) {
}
