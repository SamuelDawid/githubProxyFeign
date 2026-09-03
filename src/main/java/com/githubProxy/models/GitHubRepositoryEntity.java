package com.githubProxy.models;

import com.githubProxy.dto.GitHubRepositoryPutCommand;
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
    public boolean equals(Object o){
        if (this == o){return  true;}
        if(!(o instanceof GitHubRepositoryEntity other)){
            return false;
        }
        return id != null && id.equals(other.getId());
    }
    @Override
    public int hashCode(){return getClass().hashCode();}

    public void updateLocalRepository( GitHubRepositoryPutCommand putCommand){
        if(putCommand.fullName() != null){
            this.fullName = putCommand.fullName();
        }
        if(putCommand.cloneUrl() != null){
            this.cloneUrl = putCommand.cloneUrl();
        }
        if(putCommand.description() != null) {
        this.description = putCommand.description();
        }
        if(putCommand.stars() != null){
            this.stargazersCount = putCommand.stars();
        }
        if(putCommand.createdAt() != null){
            this.createdAt = putCommand.createdAt();
        }
    }
}
