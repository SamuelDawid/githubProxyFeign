package com.githubProxy.dto;

import java.time.LocalDateTime;

public record ErrorMessageDto(
        Integer status,
        String message,
        LocalDateTime occurredAt
) {
}
