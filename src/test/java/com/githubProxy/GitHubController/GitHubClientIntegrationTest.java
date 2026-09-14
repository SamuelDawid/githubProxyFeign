package com.githubProxy.GitHubController;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.exceptions.GitHubUnavailableException;
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
        stubFor(get("/SamuelDawid/medical-clinic").willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBodyFile("realGitHubResponse.json")));
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
        stubFor(get("/SamuelDawid/medical-clinic")
                .inScenario("retry")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("attempt2"));
        stubFor(get("/SamuelDawid/medical-clinic")
                .inScenario("retry")
                .whenScenarioStateIs("attempt2")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBodyFile("realGitHubResponse.json")));

        RepositoryDto repositoryDto = service.getByOwnerAndRepositoryName("SamuelDawid", "medical-clinic");
        assertEquals("SamuelDawid/medical-clinic", repositoryDto.fullName());
        verify(2, getRequestedFor(urlEqualTo("/SamuelDawid/medical-clinic")));
    }

    @Test
    void create_ShouldCreateGitHubEntityAndShouldMapRepositoryDtoToEntity() {
        stubFor(get("/SamuelDawid/medical-clinic").willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type","application/json")
                .withBodyFile("realGitHubResponse.json")
        ));
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
    @Test
    void getByOwnerAndRepositoryName_WhenConnectionFails_ShouldThrowGitHubUnavailable(){
        //Given
        stubFor(get("/owner/repo").willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
        //When + Then
        assertThrows(GitHubUnavailableException.class,
                () -> service.getByOwnerAndRepositoryName("owner","repo"));
    }

    @Test
    void getByOwnerAndRepositoryName_WhenTimeout_ShouldThrowGitHubUnavailable(){
        //Given
        stubFor(get("/owner/repo").willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(3000)));
        //When + Then
        assertThrows(GitHubUnavailableException.class,
                () -> service.getByOwnerAndRepositoryName("owner", "repo"));
    }

    @Test
    void  getByOwnerAndRepositoryName_WhenNotFound_ShouldNotTriggerFallback(){
        //Given
        stubFor(get("/owner/nieistniejace")
                .willReturn(aResponse().withStatus(404)));
        assertThrows(RepositoryNotFoundException.class,
                () -> service.getByOwnerAndRepositoryName("owner", "nieistniejace"));
    }
}
