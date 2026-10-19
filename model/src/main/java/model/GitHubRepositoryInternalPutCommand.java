package model;

import java.time.OffsetDateTime;

public record GitHubRepositoryInternalPutCommand(
        String fullName,
        String description,
        String cloneUrl,
        Long stars,
        OffsetDateTime createdAt
) {
}
