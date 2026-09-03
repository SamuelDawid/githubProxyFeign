package com.githubProxy.GitHubController;

import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.GitHubRepositoryPutCommand;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
import com.githubProxy.exceptions.LocalRepositoryNotFoundException;
import com.githubProxy.exceptions.RepositoryAlreadyExistsException;
import com.githubProxy.exceptions.RepositoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class GitHubClientControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    GitHubClientService service;

    @Test
    void getByOwnerAndRepositoryName_WhenOwnerAndRepositoryExists_ShouldReturn200() throws Exception {
        //Given
        String owner = "owner";
        String repoName = "OlalalNoweRepo";
        RepositoryDto expected = new RepositoryDto(
                "Owner owner",
                "o la la takie fajne",
                "htpp://api.github.urlToClone",
                4L,
                OffsetDateTime.of(LocalDateTime.of(2021, 2, 22, 12, 22), ZoneOffset.UTC)
        );
        when(service.getByOwnerAndRepositoryName(owner, repoName)).thenReturn(expected);
        //When + Then
        mockMvc.perform(get("/repositories/{owner}/{repository-name}", owner, repoName))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.fullName").value("Owner owner"),
                        jsonPath("$.description").value("o la la takie fajne"),
                        jsonPath("$.cloneUrl").value("htpp://api.github.urlToClone"),
                        jsonPath("$.createdAt").value("2021-02-22T12:22:00Z"),
                        jsonPath("$.stargazersCount").value(4)
                );

    }

    @Test
    void getByOwnerAndRepositoryName_WhenOwnerExistsButRepositoryNotExists_ShouldReturn404() throws Exception {
        //Given
        String owner = "owner";
        String repoName = "SomeRepoCoNieIstnieje";
        String url = "http://api.git/repos/owner/SomeRepoCoNieIstnieje";
        when(service.getByOwnerAndRepositoryName(owner, repoName)).thenThrow(new RepositoryNotFoundException(url));
        //When + Then
        mockMvc.perform(get("/repositories/{owner}/{repository-name}", owner, repoName))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Repository not found: " + url)
                );
    }

    @Test
    void create_WhenOwnerExistsAndFoundMatchingRepository_ShouldReturn201() throws Exception {
        //Given
        String owner = "Owner";
        String repoName = "SomeRepoCoIstnieje";
        GitHubRepositoryDto expected = new GitHubRepositoryDto(
                "Owner/SomeRepoCoIstnieje",
                "Some nice description",
                "http://api.git/repos/Owner/SomeRepoCoIstnieje",
                3L,
                OffsetDateTime.of(LocalDateTime.of(2021, 2, 22, 12, 22), ZoneOffset.UTC)
        );
        when(service.create(owner, repoName)).thenReturn(expected);
        //When + Then
        mockMvc.perform(post("/repositories/{owner}/{repository-name}", owner, repoName))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.fullName").value("Owner/SomeRepoCoIstnieje"),
                        jsonPath("$.description").value("Some nice description"),
                        jsonPath("$.stars").value(3),
                        jsonPath("$.createdAt").value("2021-02-22T12:22:00Z"),
                        jsonPath("$.cloneUrl").value("http://api.git/repos/Owner/SomeRepoCoIstnieje")
                );
    }

    @Test
    void create_WhenRepositoryAlreadyExistsInOwnerRepository_ShouldReturn409() throws Exception {
        //Given
        String owner = "Owner";
        String repoName = "SomeRepoCoIstnieje";
        when(service.create(owner, repoName)).thenThrow(new RepositoryAlreadyExistsException(owner, repoName));
        //When + Then
        mockMvc.perform(post("/repositories/{owner}/{repository-name}", owner, repoName))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.message").value(owner + " already has repository " + repoName),
                        jsonPath("$.status").value(409)
                );
    }

    @Test
    void getLocalRepositoryByOwnerAndRepositoryName_WhenLocalRepositoryExists_ShouldReturn200() throws Exception {
        //Given
        String owner = "Owner";
        String repoName = "SomeRepoCoIstnieje";
        GitHubRepositoryDto expected = new GitHubRepositoryDto(
                "Owner/SomeRepoCoIstnieje",
                "Some nice description",
                "http://api.git/repos/Owner/SomeRepoCoIstnieje",
                3L,
                OffsetDateTime.of(LocalDateTime.of(2021, 2, 22, 12, 22), ZoneOffset.UTC)

        );
        when(service.getLocalRepositoryByOwnerAndRepositoryName(owner, repoName)).thenReturn(expected);
        //When
        mockMvc.perform(get("/local/repositories/{owner}/{repository-name}", owner, repoName))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.fullName").value("Owner/SomeRepoCoIstnieje"),
                        jsonPath("$.description").value("Some nice description"),
                        jsonPath("$.stars").value(3),
                        jsonPath("$.createdAt").value("2021-02-22T12:22:00Z"),
                        jsonPath("$.cloneUrl").value("http://api.git/repos/Owner/SomeRepoCoIstnieje")

                );
    }

    @Test
    void getLocalRepositoryByOwnerAndRepositoryName_WhenLocalRepositoryDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        String owner = "Owner";
        String repoName = "SomeRepoCoIstnieje";
        when(service.getLocalRepositoryByOwnerAndRepositoryName(owner, repoName)).thenThrow(new LocalRepositoryNotFoundException(owner, repoName));
        //When + Then
        mockMvc.perform(get("/local/repositories/{owner}/{repository-name}", owner, repoName))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Local repository " + repoName + " not found for: " + owner),
                        jsonPath("$.status").value(404)
                );
    }
    @Test
    void update_WhenLocalRepositoryExists_ShouldReturn200() throws Exception {
        //Given
        String owner = "Owner";
        String repoName = "SomeRepoCoIstnieje";
        GitHubRepositoryPutCommand putCommand = new GitHubRepositoryPutCommand(
                "Owner/SomeRepoCoIstnieje",
                "newDescription",
                null,
                1L,
                null
        );
        GitHubRepositoryDto expected = new GitHubRepositoryDto(
                "Owner/SomeRepoCoIstnieje",
                "newDescription",
                "http://api.git/repos/Owner/SomeRepoCoIstnieje",
                1L,
                OffsetDateTime.of(LocalDateTime.of(2021, 2, 22, 12, 22), ZoneOffset.UTC)
        );
        when(service.update(owner,repoName,putCommand)).thenReturn(expected);
        //When + Then
        mockMvc.perform(put("/repositories/{owner}/{repository-name}",owner,repoName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(putCommand)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.fullName").value("Owner/SomeRepoCoIstnieje"),
                        jsonPath("$.description").value("newDescription"),
                        jsonPath("$.stars").value(1),
                        jsonPath("$.createdAt").value("2021-02-22T12:22:00Z"),
                        jsonPath("$.cloneUrl").value("http://api.git/repos/Owner/SomeRepoCoIstnieje")
                );
    }
    @Test
    void delete_WhenRepositoryExists_ShouldReturn204() throws Exception {
        //Given
        String owner = "Owner";
        String repoName = "SomeRepoCoIstnieje";
        mockMvc.perform(delete("/repositories/{owner}/{repository-name}",owner,repoName)).andExpect(status().isNoContent());
        verify(service).delete(owner,repoName);
    }
}