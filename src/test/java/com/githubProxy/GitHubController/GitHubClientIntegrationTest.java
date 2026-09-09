package com.githubProxy.GitHubController;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.exceptions.RepositoryNotFoundException;
import com.githubProxy.models.GitHubRepositoryEntity;
import com.githubProxy.repositories.GitHubRepositoriesRepository;
import feign.RetryableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;

import java.time.OffsetDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableWireMock
@ActiveProfiles("test")
public class GitHubClientIntegrationTest {

    @Autowired
    private GitHubClientService service;
    @Autowired
    private GitHubRepositoriesRepository repository;

    @AfterEach
    void resetWireMock() {
        WireMock.reset();
    }

    @Test
    void getByOwnerAndRepositoryName_ShouldMapRepositoryDto() {
        String owner = "SamuelDawid";
        String repository = "medical-clinic";
        String githubResponse = """
                {
                  "full_name": "SamuelDawid/medical-clinic",
                  "description": null,
                  "clone_url": "https://github.com/SamuelDawid/medical-clinic.git",
                  "stargazers_count": 0,
                  "created_at": "2026-07-13T12:04:30Z"
                }
                """;
        stubFor(get("/SamuelDawid/medical-clinic").willReturn(okJson(githubResponse)));
        RepositoryDto result = service.getByOwnerAndRepositoryName(owner, repository);
        assertEquals("SamuelDawid/medical-clinic", result.fullName());
        verify(getRequestedFor(urlEqualTo("/SamuelDawid/medical-clinic")));
    }

    @Test
    void getByOwnerAndRepositoryName_ShouldReturn404() {
        stubFor(get("/owner/nieIstniejaceRepo").willReturn(aResponse().withStatus(404)));
        assertThrows(RepositoryNotFoundException.class,
                () -> service.getByOwnerAndRepositoryName("owner", "nieIstniejaceRepo"));
    }

    @Test
    void getByOwnerAndRepositoryName_ShouldRetryThreeTimesAndReturn503() {
        stubFor(get("/owner/repo").inScenario("retry")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("attempt2"));

        stubFor(get("/owner/repo").inScenario("retry")
                .whenScenarioStateIs("attempt2")
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("attempt3"));

        stubFor(get("/owner/repo").inScenario("retry")
                .whenScenarioStateIs("attempt3")
                .willReturn(aResponse().withStatus(503)));
        RetryableException exception = assertThrows(RetryableException.class,
                () -> service.getByOwnerAndRepositoryName("owner", "repo"));
        assertEquals(503, exception.status());
        verify(3, getRequestedFor(urlEqualTo("/owner/repo")));
    }

    @Test
    void getByOwnerAndRepositoryName_ShouldRetryOnceAndReturnResponseDto() {
        String githubResponse = """
                {
                  "full_name": "SamuelDawid/medical-clinic",
                  "description": null,
                  "clone_url": "https://github.com/SamuelDawid/medical-clinic.git",
                  "stargazers_count": 0,
                  "created_at": "2026-07-13T12:04:30Z"
                }
                """;

        stubFor(get("/SamuelDawid/medical-clinic")
                .inScenario("retry")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("attempt2"));
        stubFor(get("/SamuelDawid/medical-clinic")
                .inScenario("retry")
                .whenScenarioStateIs("attempt2")
                .willReturn(okJson(githubResponse)));

        RepositoryDto repositoryDto = service.getByOwnerAndRepositoryName("SamuelDawid", "medical-clinic");
        assertEquals("SamuelDawid/medical-clinic", repositoryDto.fullName());
        verify(2, getRequestedFor(urlEqualTo("/SamuelDawid/medical-clinic")));
    }

    @Test
    void create_ShouldCreateGitHubEntityAndShouldMapRepositoryDtoToEntity() {
        String githubResponse = """
                {
                  "full_name": "SamuelDawid/medical-clinic",
                  "description": null,
                  "clone_url": "https://github.com/SamuelDawid/medical-clinic.git",
                  "stargazers_count": 0,
                  "created_at": "2026-07-13T12:04:30Z"
                }
                """;
        stubFor(get("/SamuelDawid/medical-clinic").willReturn(okJson(githubResponse)));
        service.create("SamuelDawid", "medical-clinic");
        assertTrue(repository.existsByOwnerAndRepositoryName("SamuelDawid", "medical-clinic"));
        GitHubRepositoryEntity saved = repository
                .findByOwnerAndRepositoryName("SamuelDawid", "medical-clinic")
                .orElseThrow();

        assertAll(
                () -> assertEquals("SamuelDawid/medical-clinic", saved.getFullName()),
                () -> assertEquals("https://github.com/SamuelDawid/medical-clinic.git", saved.getCloneUrl()),
                () -> assertEquals(0L, saved.getStargazersCount()),
                () -> assertEquals(OffsetDateTime.parse("2026-07-13T12:04:30Z"), saved.getCreatedAt())
        );
    }
}
