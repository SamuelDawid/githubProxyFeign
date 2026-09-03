package com.githubProxy.customErrorDecoder;

import com.githubProxy.exceptions.RepositoryNotFoundException;
import com.githubProxy.exceptions.handler.GitHubClientException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class GitHubClientErrorDecoderTest {

    private GitHubClientErrorDecoder decoder;

    @BeforeEach
    void setUp() {
        this.decoder = new GitHubClientErrorDecoder();
    }

    private Response responseWithStatus(int status, String reason) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "https://api.github.com/repos/owner/repo",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        return Response.builder()
                .status(status)
                .reason(reason)
                .request(request)
                .headers(Map.of())
                .build();
    }

    @Test
    void decode_When404_ShouldReturnRepositoryNotFoundException() {
        //Given
        Response response = responseWithStatus(404, "Not Found");
        //When + Then
        Exception result = decoder.decode("GitHubClient#getRepo(String,String)", response);
        assertInstanceOf(RepositoryNotFoundException.class, result);
    }

    @Test
    void decode_When502_ShouldReturnGitHubClientException() {
        //Given
        Response response = responseWithStatus(502, "Server error");
        //When = Then
        Exception result = decoder.decode("GitHubClient#getRepo(String,String)", response);
        assertEquals(GitHubClientException.class, result.getClass());
    }
}