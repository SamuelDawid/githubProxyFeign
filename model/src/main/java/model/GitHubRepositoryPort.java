package model;

import java.util.Optional;

public interface GitHubRepositoryPort {
    RepositoryPOJO save(RepositoryPOJO repository);
    Optional<RepositoryPOJO> findById(Long id);
    Optional<RepositoryPOJO> findByOwnerAndRepositoryName(String owner, String repositoryName);
    boolean existsByOwnerAndRepositoryName(String owner, String repositoryName);
    void delete(RepositoryPOJO repository);

}
