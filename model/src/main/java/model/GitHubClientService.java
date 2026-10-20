package model;

import lombok.NonNull;

public interface GitHubClientService {
    GitHubResponse getByOwnerAndRepositoryName(@NonNull String owner, @NonNull String repositoryName);

    RepositoryPOJO getLocalRepositoryByOwnerAndRepositoryName(@NonNull String owner, @NonNull String repositoryName);

    RepositoryPOJO create(@NonNull String owner, @NonNull String repositoryName);

    RepositoryPOJO update(@NonNull String owner, @NonNull String repositoryName, @NonNull GitHubRepositoryInternalPutCommand putCommand);

    void delete(@NonNull String owner, @NonNull String repositoryName);
}
