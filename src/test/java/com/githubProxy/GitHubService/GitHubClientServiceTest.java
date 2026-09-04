package com.githubProxy.GitHubService;

import com.githubProxy.dto.GitHubRepositoryPutCommand;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
import com.githubProxy.exceptions.LocalRepositoryNotFoundException;
import com.githubProxy.exceptions.RepositoryAlreadyExistsException;
import com.githubProxy.exceptions.RepositoryNotFoundException;
import com.githubProxy.gitHubClient.GitHubClient;
import com.githubProxy.gitHubClient.GitHubResponse;
import com.githubProxy.mappers.GitHubClientMapper;
import com.githubProxy.models.GitHubRepositoryEntity;
import com.githubProxy.repositories.GitHubRepositoriesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GitHubClientServiceTest {

    private GitHubClient gitHubClient;
    private GitHubClientMapper gitHubClientMapper;
    private GitHubClientService service;
    private GitHubRepositoriesRepository repository;

    @BeforeEach
    void setUp() {
        this.gitHubClient = Mockito.mock(GitHubClient.class);
        this.gitHubClientMapper = Mappers.getMapper(GitHubClientMapper.class);
        this.repository = Mockito.mock(GitHubRepositoriesRepository.class);
        this.service = new GitHubClientService(gitHubClient, gitHubClientMapper, repository);
    }


    @Test
    void getByOwnerAndRepositoryName_WhenRepositoryExistsShouldReturnMatchingDto() {
        //Given
        String owner = "Samuel";
        String repositoryName = "newAppOlalal";
        GitHubResponse response = new GitHubResponse(
                "Samuel/newAppOlalal", null,
                "http://api.github.someUrl",
                0L,
                OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC));
        when(gitHubClient.getByOwnerAndRepositoryName(owner, repositoryName)).thenReturn(response);
        //When
        RepositoryDto result = service.getByOwnerAndRepositoryName(owner, repositoryName);
        //Then
        Assertions.assertAll(
                () -> assertEquals("Samuel/newAppOlalal", result.fullName()),
                () -> assertEquals("http://api.github.someUrl", result.cloneUrl()),
                () -> assertNull(result.description()),
                () -> assertEquals(0L, result.stargazersCount()),
                () -> assertEquals("2000-03-11T15:12Z", result.createdAt().toString()),
                () -> assertEquals(OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC), result.createdAt())
        );

    }

    @Test
    void getByOwnerAndRepositoryName_WhenRepositoryDoesNotExists_ShouldThrowRepositoryNotFoundException() {
        //Given
        String owner = "Owner";
        String repoName = "NotExistingOne";
        when(gitHubClient.getByOwnerAndRepositoryName(owner, repoName)).thenThrow(RepositoryNotFoundException.class);
        //When + Then
        assertThrows(RepositoryNotFoundException.class,
                () -> service.getByOwnerAndRepositoryName(owner, repoName));
    }

    @Test
    void getLocalRepositoryByOwnerAndRepositoryName_WhenRepositoryExists_ShouldReturnMatchingDto() {
        //Given
        String owner = "owner";
        String repoName = "SomeExistingRepository";
        GitHubRepositoryEntity returned = buildRepositoryEntity(
                1L,
                "owner",
                "SomeExistingRepository",
                "owner/SomeExistingRepository",
                "Some desctiption",
                "http://api.github.someUrl",
                6L,
                OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC)
        );
        GitHubRepositoryDto expected = new GitHubRepositoryDto(
                "owner/SomeExistingRepository",
                "Some desctiption",
                "http://api.github.someUrl",
                6L,
                OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC)
        );
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.of(returned));
        //When
        GitHubRepositoryDto result = service.getLocalRepositoryByOwnerAndRepositoryName(owner, repoName);
        //Then
        assertAll(
                () -> assertEquals(expected.fullName(), result.fullName()),
                () -> assertEquals(expected.cloneUrl(), result.cloneUrl()),
                () -> assertEquals(expected.createdAt(), result.createdAt()),
                () -> assertEquals(expected.description(), result.description()),
                () -> assertEquals(expected.stars(), result.stars())
        );
    }

    @Test
    void getLocalRepositoryByOwnerAndRepositoryName_WhenLocalRepositoryDoesNotExists_ShouldThrowLocalRepositoryNotFoundException() {
        //Given
        String owner = "owner";
        String repoName = "SomeExistingRepository";
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.empty());
        //When + Then
        LocalRepositoryNotFoundException exception = assertThrowsExactly(LocalRepositoryNotFoundException.class,
                () -> service.getLocalRepositoryByOwnerAndRepositoryName(owner, repoName));
        assertEquals("Local repository " + repoName + " not found for: " + owner, exception.getMessage());
    }


    @Test
    void create_WhenRepositoryAndOwnerDoesExists_ShouldReturnMappedDto() {
        //Given
        String owner = "Owner";
        String repoName = "ExistingRepo";
        GitHubRepositoryEntity expected = new GitHubRepositoryEntity(
                owner,
                repoName,
                "Owner/ExistingRepo",
                "Some description",
                "http://api.github.someUrl",
                3L,
                OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC)
        );
        when(repository.save(any(GitHubRepositoryEntity.class))).thenReturn(expected);
        //When
        GitHubRepositoryDto result = service.create(owner, repoName);
        verify(repository).save(any(GitHubRepositoryEntity.class));
        // Then
        assertAll(
                () -> assertEquals("http://api.github.someUrl", result.cloneUrl()),
                () -> assertEquals("Some description", result.description()),
                () -> assertEquals(3L, result.stars()),
                () -> assertEquals(OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC), result.createdAt())

        );
    }

    @Test
    void create_WhenRepositoryAndOwnerDoesExists_ShouldMapGitHubResponseToEntity() {
        //Given
        String owner = "Owner";
        String repoName = "SomeOtherExistingRepo";
        GitHubResponse response = new GitHubResponse(
                "Owner/SomeOtherExistingRepo",
                "Some other fascinating description",
                "http://api.github.someUrl",
                1L,
                OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC)

        );
        when(gitHubClient.getByOwnerAndRepositoryName(owner, repoName)).thenReturn(response);
        service.create(owner, repoName);
        ArgumentCaptor<GitHubRepositoryEntity> captor = ArgumentCaptor.forClass(GitHubRepositoryEntity.class);
        verify(repository).save(captor.capture());
        assertAll(
                () -> assertEquals("Owner", captor.getValue().getOwner()),
                () -> assertEquals("SomeOtherExistingRepo", captor.getValue().getRepositoryName()),
                () -> assertEquals("http://api.github.someUrl", captor.getValue().getCloneUrl()),
                () -> assertEquals("Some other fascinating description", captor.getValue().getDescription()),
                () -> assertEquals(1L, captor.getValue().getStargazersCount())
        );
    }

    @Test
    void create_WhenRepositoryAlreadyExists_ShouldThrowRepositoryAlreadyExistsException() {
        //Given
        String owner = "Owner";
        String repoName = "AlreadySavedRepository";
        when(repository.existsByOwnerAndRepositoryName(owner, repoName)).thenReturn(true);
        //When + Then
        RepositoryAlreadyExistsException exception = assertThrows(RepositoryAlreadyExistsException.class,
                () -> service.create(owner, repoName));
        assertTrue(exception.getMessage().contains(owner + " already has repository " + repoName));
        verify(gitHubClient, never()).getByOwnerAndRepositoryName(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void update_WhenRepositoryExists_ShouldUpdateAndReturnMatchingDto() {
        //Given
        String owner = "Owner";
        String repoName = "AlreadySavedRepository";
        GitHubRepositoryEntity existing = buildRepositoryEntity(
                1L,
                owner,
                repoName,
                "Owner/ExistingRepo",
                "Some description",
                "http://api.github.someUrl",
                3L,
                OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC)
        );
        GitHubRepositoryPutCommand putCommand = new GitHubRepositoryPutCommand(
                null,
                "Some new fascinating description",
                null,
                10L,
                null
        );
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.of(existing));
        //When
        GitHubRepositoryDto result = service.update(owner, repoName, putCommand);
        //Then
        assertAll(
                () -> assertEquals("Owner/ExistingRepo", result.fullName()),
                () -> assertEquals("Some new fascinating description", result.description()),
                () -> assertEquals("http://api.github.someUrl", result.cloneUrl()),
                () -> assertEquals(10L, result.stars()),
                () -> assertEquals(OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC), result.createdAt())
        );
    }

    @Test
    void update_WhenRepositoryDoesNotExist_ShouldThrowRepositoryNotFoundException() {
        //Given
        String owner = "Owner";
        String repoName = "NonExistingRepository";
        GitHubRepositoryPutCommand command = new GitHubRepositoryPutCommand(
                null,
                null,
                null,
                null,
                null
        );
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.empty());
        //When + Then
        LocalRepositoryNotFoundException exception = assertThrows(LocalRepositoryNotFoundException.class,
                () -> service.update(owner, repoName, command));
        assertEquals(exception.getMessage(), "Local repository " + repoName + " not found for: " + owner);
    }

    @Test
    void delete_WhenRepositoryExists_ShouldRemoveLocalRepository() {
        //Given
        String owner = "Owner";
        String repoName = "AlreadySavedRepository";
        GitHubRepositoryEntity existing = buildRepositoryEntity(
                1L,
                owner,
                repoName,
                "Owner/ExistingRepo",
                "Some description",
                "http://api.github.someUrl",
                3L,
                OffsetDateTime.of(LocalDateTime.of(2000, 3, 11, 15, 12), ZoneOffset.UTC)
        );
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.of(existing));
        //When + Then
        service.delete(owner, repoName);
        verify(repository).delete(existing);
    }

    @Test
    void delete_WhenRepositoryDoesNotExist_ShouldThrowRepositoryNotFoundException() {
        //Given
        String owner = "Owner";
        String repoName = "NonExistingRepository";
        when(repository.findByOwnerAndRepositoryName(owner, repoName)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(LocalRepositoryNotFoundException.class,
                () -> service.delete(owner, repoName));
        verify(repository, never()).delete(any(GitHubRepositoryEntity.class));
    }

    private GitHubRepositoryEntity buildRepositoryEntity(Long id, String owner, String repositoryName, String fullName, String description, String cloneUrl, Long stargazersCount, OffsetDateTime createdAt) {
        GitHubRepositoryEntity entity = new GitHubRepositoryEntity(owner, repositoryName, fullName, description, cloneUrl, stargazersCount, createdAt);
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}