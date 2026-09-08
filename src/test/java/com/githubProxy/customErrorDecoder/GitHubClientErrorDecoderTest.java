package com.githubProxy.customErrorDecoder;

import com.githubProxy.exceptions.RepositoryNotFoundException;
import com.githubProxy.exceptions.handler.GitHubClientException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    void decode_When502_ShouldReturnRetryableException() {
        //Given
        Response response = responseWithStatus(502, "Server error");
        //When = Then
        Exception result = decoder.decode("GitHubClient#getRepo(String,String)", response);
        assertEquals(RetryableException.class, result.getClass());
    }
    @Test
    void decode_When503_ShouldReturnRetryableException() {
        //Given
        Response response = responseWithStatus(503, "Service Unavailable");
        //When = Then
        Exception result = decoder.decode("GitHubClient#getRepo(String,String)", response);
        assertEquals(RetryableException.class, result.getClass());
    }
    @Test
    void decode_When504_ShouldReturnRetryableException() {
        //Given
        Response response = responseWithStatus(504, "Gateway Timeout");
        //When = Then
        Exception result = decoder.decode("GitHubClient#getRepo(String,String)", response);
        assertEquals(RetryableException.class, result.getClass());
    }
    @Test
    void decode_When500_ShouldReturnGitHubClientException(){
        //Given
        Response response = responseWithStatus(500,"Bad Request");
        //When + Then
        Exception result = decoder.decode("GitHubClient#getRepo(String,String)", response);
        assertEquals(GitHubClientException.class,result.getClass());
    }
}