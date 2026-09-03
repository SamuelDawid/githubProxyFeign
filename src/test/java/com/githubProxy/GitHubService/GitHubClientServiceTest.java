package com.githubProxy.GitHubService;

import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GitHubClientServiceTest {

    private GitHubClient gitHubClient;
    private GitHubClientMapper gitHubClientMapper;
    private GitHubClientService service;
    private GitHubRepositoriesRepository repository;
    @BeforeEach
    void setUp(){
        this.gitHubClient = Mockito.mock(GitHubClient.class);
        this.gitHubClientMapper = Mappers.getMapper(GitHubClientMapper.class);
        this.repository = Mockito.mock(GitHubRepositoriesRepository.class);
        this.service = new GitHubClientService(gitHubClient,gitHubClientMapper,repository);
    }


    @Test
    void getByOwnerAndRepositoryName_WhenRepositoryExistsShouldReturnMatchingDto(){
       //Given
        String owner = "Samuel";
        String repositoryName = "newAppOlalal";
        GitHubResponse response = new GitHubResponse(
                "Samuel/newAppOlalal",null,
                "http://api.github.someUrl",
                0L,
                OffsetDateTime.of(LocalDateTime.of(2000,3,11,15,12),ZoneOffset.UTC));
        when(gitHubClient.getByOwnerAndRepositoryName(owner,repositoryName)).thenReturn(response);
       //When
        RepositoryDto result = service.getByOwnerAndRepositoryName(owner,repositoryName);
        //Then
        Assertions.assertAll(
                () -> assertEquals("Samuel/newAppOlalal",result.fullName()),
                () -> assertEquals("http://api.github.someUrl",result.cloneUrl()),
                () -> assertNull(result.description()),
                () -> assertEquals(0L,result.stargazersCount()),
                () -> assertEquals("2000-03-11T15:12Z",result.createdAt().toString()),
                () -> assertEquals(OffsetDateTime.of(LocalDateTime.of(2000,3,11,15,12),ZoneOffset.UTC), result.createdAt())
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
    void create_WhenRepositoryAndOwnerDoesExists_ShouldReturnMatchingDto(){
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
                OffsetDateTime.of(LocalDateTime.of(2000,3,11,15,12),ZoneOffset.UTC)

        );
        when(repository.save(any(GitHubRepositoryEntity.class))).thenReturn(expected);
        //When
        GitHubRepositoryDto result =service.create(owner,repoName);
        verify(repository).save(any(GitHubRepositoryEntity.class));
        // Then
        assertAll(
                () -> assertEquals("Owner",result.owner()),
                () -> assertEquals("ExistingRepo",result.repositoryName()),
                () -> assertEquals("http://api.github.someUrl",result.cloneUrl()),
                () -> assertEquals("Some description",result.description()),
                () -> assertEquals(3L,result.stargazersCount()),
                () -> assertEquals(OffsetDateTime.of(LocalDateTime.of(2000,3,11,15,12),ZoneOffset.UTC),result.createdAt())

        );
    }
    @Test
    void create_WhenRepositoryAlreadyExists_ShouldThrowRepositoryAlreadyExistsException(){
        //Given
        String owner = "Owner";
        String repoName = "AlreadySavedRepository";
        when(repository.existsByOwnerAndRepositoryName(owner,repoName)).thenReturn(true);
        //When + Then
        RepositoryAlreadyExistsException exception = assertThrows(RepositoryAlreadyExistsException.class,
                () -> service.create(owner,repoName));
        assertTrue(exception.getMessage().contains(owner + " already has repository " + repoName));
    }
}