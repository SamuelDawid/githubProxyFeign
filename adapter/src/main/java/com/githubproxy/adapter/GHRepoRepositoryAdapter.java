package com.githubproxy.adapter;

import lombok.RequiredArgsConstructor;
import com.githubProxy.GitHubRepositoryPort;
import model.RepositoryPOJO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GHRepoRepositoryAdapter implements GitHubRepositoryPort {
    private final GitHubEntityJpaRepository jpaRepository;
    private final GitHubClientMapper mapper;

    @Override
    public RepositoryPOJO save(RepositoryPOJO repository) {
        GitHubRepositoryEntity entity = mapper.toEntityFromPojo(repository);
        GitHubRepositoryEntity saved = jpaRepository.save(entity);
        return mapper.toPojo(saved);
    }

    @Override
    public Optional<RepositoryPOJO> findByOwnerAndRepositoryName(String owner, String repositoryName) {
        return jpaRepository.findByOwnerAndRepositoryName(owner, repositoryName).map(mapper::toPojo);
    }

    @Override
    public boolean existsByOwnerAndRepositoryName(String owner, String repositoryName) {
        return jpaRepository.existsByOwnerAndRepositoryName(owner, repositoryName);
    }

    @Override
    public void delete(RepositoryPOJO repository) {
        GitHubRepositoryEntity entity = mapper.toEntityFromPojo(repository);
        jpaRepository.delete(entity);
    }
}
