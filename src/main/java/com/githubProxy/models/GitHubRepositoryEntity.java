package com.githubProxy.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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
}
