package com.githubproxy.adapter.feign;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record GitHubApiResponse(
        @JsonProperty("full_name")
        String fullName,
        String description,
        @JsonProperty("clone_url")
        String cloneUrl,
        @JsonProperty("stargazers_count")
        Long stargazersCount,
        @JsonProperty("created_at")
        OffsetDateTime createdAt
) {
}
