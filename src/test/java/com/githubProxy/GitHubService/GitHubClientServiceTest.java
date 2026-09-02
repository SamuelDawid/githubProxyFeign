package com.githubProxy.GitHubService;

import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.exceptions.RepositoryNotFoundException;
import com.githubProxy.gitHubClient.GitHubClient;
import com.githubProxy.gitHubClient.GitHubResponse;
import com.githubProxy.mappers.GitHubClientMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GitHubClientServiceTest {

    private GitHubClient gitHubClient;
    private GitHubClientMapper gitHubClientMapper;
    private GitHubClientService service;

    @BeforeEach
    void setUp(){
        this.gitHubClient = Mockito.mock(GitHubClient.class);
        this.gitHubClientMapper = Mappers.getMapper(GitHubClientMapper.class);
        this.service = new GitHubClientService(gitHubClient,gitHubClientMapper);
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
                LocalDateTime.of(2000,3,11,15,12));
        when(gitHubClient.getByOwnerAndRepositoryName(owner,repositoryName)).thenReturn(response);
       //When
        RepositoryDto result = service.getByOwnerAndRepositoryName(owner,repositoryName);
        //Then
        Assertions.assertAll(
                () -> assertEquals("Samuel/newAppOlalal",result.fullName()),
                () -> assertEquals("http://api.github.someUrl",result.cloneUrl()),
                () -> assertNull(result.description()),
                () -> assertEquals(0L,result.stargazersCount()),
                () -> assertEquals("2000-03-11T15:12",result.createdAt().toString()),
                () -> assertEquals(LocalDateTime.of(2000, 3, 11, 15, 12), result.createdAt())
        );

    }
    @Test
    void getByOwnerAndRepositoryName_WhenRepositoryDoesNotExists_ShouldThrowRepositoryNotFoundException() {
        //Given
        String owner = "Owner";
        String reponame = "NotExistingOne";
        when(gitHubClient.getByOwnerAndRepositoryName(owner,reponame)).thenThrow(RepositoryNotFoundException.class);
        //When + Then
        assertThrows(RepositoryNotFoundException.class,
                () -> service.getByOwnerAndRepositoryName(owner,reponame));
    }
}