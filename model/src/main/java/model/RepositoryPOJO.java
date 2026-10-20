package model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class RepositoryPOJO {
    private Long id;
    private String owner;
    private String repositoryName;
    private String fullName;
    private String description;
    private String cloneUrl;
    private Long stargazersCount;
    private OffsetDateTime createdAt;

    public static RepositoryPOJO create(GitHubResponse response, String owner, String repositoryName){
        return RepositoryPOJO.builder()
                .id(null)
                .owner(owner)
                .repositoryName(repositoryName)
                .fullName(response.fullName)
                .description(response.description)
                .cloneUrl(response.cloneUrl)
                .stargazersCount(response.stargazersCount)
                .createdAt(response.createdAt)
                .build();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepositoryPOJO other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void updateLocalRepository(GitHubRepositoryInternalPutCommand putCommand) {
        if (putCommand.fullName() != null) {
            this.fullName = putCommand.fullName();
        }
        if (putCommand.cloneUrl() != null) {
            this.cloneUrl = putCommand.cloneUrl();
        }
        if (putCommand.description() != null) {
            this.description = putCommand.description();
        }
        if (putCommand.stars() != null) {
            this.stargazersCount = putCommand.stars();
        }
        if (putCommand.createdAt() != null) {
            this.createdAt = putCommand.createdAt();
        }
    }
}
