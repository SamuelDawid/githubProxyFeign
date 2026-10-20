package com.githubproxy.adapter;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;

@ToString
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GIT_HUB_REPOSITORY")
public class GitHubRepositoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String owner;
    @Column(nullable = false)
    private String repositoryName;
    @Column(nullable = false)
    private String fullName;
    private String description;
    private String cloneUrl;
    private Long stargazersCount;
    private OffsetDateTime createdAt;

    public GitHubRepositoryEntity(String owner, String repositoryName, String fullName, String description, String cloneUrl, Long stargazersCount, OffsetDateTime createdAt) {
        this.owner = owner;
        this.repositoryName = repositoryName;
        this.fullName = fullName;
        this.description = description;
        this.cloneUrl = cloneUrl;
        this.stargazersCount = stargazersCount;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GitHubRepositoryEntity other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
