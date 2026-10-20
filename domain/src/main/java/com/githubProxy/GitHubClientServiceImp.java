package com.githubProxy;

import expections.LocalRepositoryNotFoundException;
import expections.RepositoryAlreadyExistsException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.*;


@RequiredArgsConstructor
@Slf4j
public class GitHubClientServiceImp implements GitHubClientService {
    private final GitHubRepositoryPort repositoryPort;
    private final GitHubClientPort gitHubClientPort;

    public GitHubResponse getByOwnerAndRepositoryName(@NonNull String owner, @NonNull String repositoryName) {
        log.info("Finding repository with name {} by {}", repositoryName, owner);
        GitHubResponse response = gitHubClientPort.getByOwnerAndRepositoryName(owner,repositoryName);
        log.info("Repository {} found with this details : {}", repositoryName, response);
        return response;
    }

    public RepositoryPOJO getLocalRepositoryByOwnerAndRepositoryName(@NonNull String owner, @NonNull String repositoryName) {
        log.info("Finding local repository {} by {}", repositoryName, owner);
        return findRepositoryByOwnerAndNameOrThrow(owner, repositoryName);
    }

    public RepositoryPOJO create(@NonNull String owner, @NonNull String repositoryName) {
        log.info("Creating GitHubRepository entity for owner: {}, repository name: {}", owner, repositoryName);
        validateRepository(owner, repositoryName);
        GitHubResponse response = gitHubClientPort.getByOwnerAndRepositoryName(owner, repositoryName);
        log.info("Found github repository {}", response);
        RepositoryPOJO entity = RepositoryPOJO.create(response,owner,repositoryName);
        RepositoryPOJO saved = repositoryPort.save(entity);
        log.info("Successfully Created Repository entity {}", saved);
        return saved;
    }

    public RepositoryPOJO update(@NonNull String owner, @NonNull String repositoryName, @NonNull GitHubRepositoryInternalPutCommand putCommand) {
        log.info("Stared updating local repository {} by {}", owner, repositoryName);
        RepositoryPOJO entity = findRepositoryByOwnerAndNameOrThrow(owner, repositoryName);
        entity.updateLocalRepository(putCommand);
        RepositoryPOJO saved = repositoryPort.save(entity);
        log.info("Successfully updated repository {} -> {}", repositoryName, entity);
        return saved;
    }


    public void delete(@NonNull String owner, @NonNull String repositoryName) {
        log.info("Removing repository {} from {}", repositoryName, owner);
        RepositoryPOJO entity = findRepositoryByOwnerAndNameOrThrow(owner, repositoryName);
        repositoryPort.delete(entity);
        log.info("Successfully removed repository {}", repositoryName);
    }

    private void validateRepository(String owner, String repositoryName) {
        if (repositoryPort.existsByOwnerAndRepositoryName(owner, repositoryName)) {
            log.error("Repository {} already exists for {}", repositoryName, owner);
            throw new RepositoryAlreadyExistsException(owner, repositoryName);
        }
    }

    private RepositoryPOJO findRepositoryByOwnerAndNameOrThrow(String owner, String repositoryName) {
        return repositoryPort.findByOwnerAndRepositoryName(owner, repositoryName).orElseThrow(() -> {
                    log.error("Local repository {} not found for {}", repositoryName, owner);
                    return new LocalRepositoryNotFoundException(owner, repositoryName);
                }
        );
    }
}
