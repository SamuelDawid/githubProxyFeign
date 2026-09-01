package com.githubProxy.gitHubClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record GitHubResponse(
        @JsonProperty("full_name")
        String fullName,
        String description,
        @JsonProperty("clone_url")
        String cloneUrl,
        @JsonProperty("stargazers_count")
        Long stargazersCount,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
