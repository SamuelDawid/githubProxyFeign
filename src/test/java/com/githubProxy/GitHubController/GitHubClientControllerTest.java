package com.githubProxy.GitHubController;

import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.exceptions.RepositoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void getByOwnerAndRepositoryName_WhenOwnerAndRepositoryExists_ShouldReturn200() throws Exception{
        //Given
        String owner = "owner";
        String repoName = "OlalalNoweRepo";
        RepositoryDto expected = new RepositoryDto(
                "Owner owner",
                "o la la takie fajne",
                "htpp://api.github.urlToClone",
                4L,
                LocalDateTime.of(2021,2,22,12,22)
        );
        when(service.getByOwnerAndRepositoryName(owner,repoName)).thenReturn(expected);
        //When + Then
        mockMvc.perform(get("/repos/{owner}/{repository-name}",owner,repoName))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.fullName").value("Owner owner"),
                        jsonPath("$.description").value("o la la takie fajne"),
                        jsonPath("$.cloneUrl").value("htpp://api.github.urlToClone"),
                        jsonPath("$.stargazersCount").value(4)
                );

    }
    @Test
    void getByOwnerAndRepositoryName_WhenOwnerExistsButRepositoryNotExists_ShouldReturn404() throws Exception{
       //Given
        String owner = "owner";
        String repoName = "SomeRepoCoNieIstnieje";
        String url = "http://api.git/repos/owner/SomeRepoCoNieIstnieje";
        when(service.getByOwnerAndRepositoryName(owner,repoName)).thenThrow(new RepositoryNotFoundException(url));
        //When + Then
        mockMvc.perform(get("/repos/{owner}/{repository-name}",owner,repoName))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.message").value("Repository not found: "+url)
                );
    }
}