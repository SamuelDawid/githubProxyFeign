package com.githubProxy.repositories;

import com.githubProxy.dto.GitHubRepositoryPutCommand;
import com.githubProxy.models.GitHubRepositoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@ActiveProfiles("test")
@DataJpaTest
public class GitHubRepositoryRepositoryTest {
    @Autowired
    private GitHubRepositoriesRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private static final OffsetDateTime CREATED_AT =
            OffsetDateTime.of(2021, 2, 22, 12, 22, 0, 0, ZoneOffset.UTC);

    private GitHubRepositoryEntity persistAndClear(GitHubRepositoryEntity entity) {
        GitHubRepositoryEntity saved = entityManager.persistAndFlush(entity);
        entityManager.clear();
        return saved;
    }

    private static GitHubRepositoryEntity entity(String owner, String repositoryName) {
        return new GitHubRepositoryEntity(
                owner,
                repositoryName,
                owner + "/" + repositoryName,
                "Some nice description",
                "http://api.git/repos/" + owner + "/" + repositoryName,
                3L,
                CREATED_AT
        );
    }

    @Test
    void findByOwnerAndRepositoryName_WhenRepositoryExists_ShouldReturnEntity() {
        //Given
        GitHubRepositoryEntity saved = persistAndClear(entity("Owner", "SomeRepo"));
        //When
        Optional<GitHubRepositoryEntity> result = repository.findByOwnerAndRepositoryName("Owner", "SomeRepo");
        //Then
        assertThat(result).isPresent();
        GitHubRepositoryEntity found = result.get();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getOwner()).isEqualTo("Owner");
        assertThat(found.getRepositoryName()).isEqualTo("SomeRepo");
        assertThat(found.getFullName()).isEqualTo("Owner/SomeRepo");
        assertThat(found.getDescription()).isEqualTo("Some nice description");
        assertThat(found.getCloneUrl()).isEqualTo("http://api.git/repos/Owner/SomeRepo");
        assertThat(found.getStargazersCount()).isEqualTo(3L);
    }

    @Test
    void findByOwnerAndRepositoryName_WhenRepositoryNameDoesNotMatch_ShouldReturnEmpty() {
        //Given
        persistAndClear(entity("Owner", "SomeRepo"));
        //When
        Optional<GitHubRepositoryEntity> result = repository.findByOwnerAndRepositoryName("Owner", "OtherRepo");
        //Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByOwnerAndRepositoryName_WhenSameRepoNameBelongsToOtherOwner_ShouldReturnEmpty() {
        //Given
        persistAndClear(entity("OtherOwner", "SomeRepo"));
        //When
        Optional<GitHubRepositoryEntity> result = repository.findByOwnerAndRepositoryName("Owner", "SomeRepo");
        //Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByOwnerAndRepositoryName_WhenManyRepositoriesExist_ShouldReturnMatchingOne() {
        //Given
        persistAndClear(entity("Owner", "RepoA"));
        GitHubRepositoryEntity target = persistAndClear(entity("Owner", "RepoB"));
        persistAndClear(entity("OtherOwner", "RepoB"));
        //When
        Optional<GitHubRepositoryEntity> result = repository.findByOwnerAndRepositoryName("Owner", "RepoB");
        //Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(target.getId());
        assertThat(result.get().getFullName()).isEqualTo("Owner/RepoB");
    }

    @Test
    void findByOwnerAndRepositoryName_WhenDatabaseIsEmpty_ShouldReturnEmpty() {
        assertThat(repository.findByOwnerAndRepositoryName("Owner", "SomeRepo")).isEmpty();
    }

    @Test
    void existsByOwnerAndRepositoryName_WhenRepositoryExists_ShouldReturnTrue() {
        //Given
        persistAndClear(entity("Owner", "SomeRepo"));
        //When + Then
        assertThat(repository.existsByOwnerAndRepositoryName("Owner", "SomeRepo")).isTrue();
    }

    @Test
    void existsByOwnerAndRepositoryName_WhenRepositoryDoesNotExist_ShouldReturnFalse() {
        //Given
        persistAndClear(entity("Owner", "SomeRepo"));
        //When + Then
        assertThat(repository.existsByOwnerAndRepositoryName("Owner", "OtherRepo")).isFalse();
    }

    @Test
    void existsByOwnerAndRepositoryName_WhenOwnerDoesNotMatch_ShouldReturnFalse() {
        //Given
        persistAndClear(entity("OtherOwner", "SomeRepo"));
        //When + Then
        assertThat(repository.existsByOwnerAndRepositoryName("Owner", "SomeRepo")).isFalse();
    }

    @Test
    void save_ShouldGenerateIdAndPersistEntity() {
        //When
        GitHubRepositoryEntity saved = repository.saveAndFlush(entity("Owner", "SomeRepo"));
        entityManager.clear();
        //Then
        assertThat(saved.getId()).isNotNull();
        assertThat(entityManager.find(GitHubRepositoryEntity.class, saved.getId())).isNotNull();
    }

    @Test
    void save_WhenOwnerIsNull_ShouldThrowDataIntegrityViolation() {
        //Given
        GitHubRepositoryEntity invalid = new GitHubRepositoryEntity(
                null, "SomeRepo", "null/SomeRepo", null, null, null, null);
        //When + Then
        assertThatThrownBy(() -> repository.saveAndFlush(invalid))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_WhenFullNameIsNull_ShouldThrowDataIntegrityViolation() {
        //Given
        GitHubRepositoryEntity invalid = new GitHubRepositoryEntity(
                "Owner", "SomeRepo", null, null, null, null, null);
        //When + Then
        assertThatThrownBy(() -> repository.saveAndFlush(invalid))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void delete_ShouldRemoveEntity() {
        //Given
        GitHubRepositoryEntity saved = persistAndClear(entity("Owner", "SomeRepo"));
        GitHubRepositoryEntity managed = repository.findById(saved.getId()).orElseThrow();
        //When
        repository.delete(managed);
        repository.flush();
        entityManager.clear();
        //Then
        assertThat(repository.existsByOwnerAndRepositoryName("Owner", "SomeRepo")).isFalse();
    }

    @Test
    void updateLocalRepository_WhenEntityIsManaged_ShouldPersistChangedFieldsOnly() {
        //Given
        GitHubRepositoryEntity saved = persistAndClear(entity("Owner", "SomeRepo"));
        GitHubRepositoryEntity managed = repository.findByOwnerAndRepositoryName("Owner", "SomeRepo").orElseThrow();
        GitHubRepositoryPutCommand command = new GitHubRepositoryPutCommand(
                null,
                "newDescription",
                null,
                10L,
                null
        );
        //When — bez wywołania save(): zmiana zarządzanej encji trafia do bazy przy flushu
        managed.updateLocalRepository(command);
        entityManager.flush();
        entityManager.clear();
        //Then
        GitHubRepositoryEntity reloaded = entityManager.find(GitHubRepositoryEntity.class, saved.getId());
        assertThat(reloaded.getDescription()).isEqualTo("newDescription");
        assertThat(reloaded.getStargazersCount()).isEqualTo(10L);
        assertThat(reloaded.getFullName()).isEqualTo("Owner/SomeRepo");
        assertThat(reloaded.getCloneUrl()).isEqualTo("http://api.git/repos/Owner/SomeRepo");
    }
}
