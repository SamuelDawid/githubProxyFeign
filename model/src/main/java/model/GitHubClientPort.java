package model;

public interface GitHubClientPort {
    GitHubResponse getByOwnerAndRepositoryName(String owner,String repositoryName);
}
