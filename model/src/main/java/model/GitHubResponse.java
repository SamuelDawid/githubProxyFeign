package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
@Builder
@Setter
@Getter
@AllArgsConstructor
public class GitHubResponse {
    String fullName;
    String description;
    String cloneUrl;
    Long stargazersCount;
    OffsetDateTime createdAt;
}
