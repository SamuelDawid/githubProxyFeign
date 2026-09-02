package com.githubProxy.customErrorDecoder;

import com.githubProxy.exceptions.RepositoryNotFoundException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

@Slf4j
public class GitHubClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String responseBody = extractResponseBody(response);
        log.error("Feign client error. Method: {}, Status: {}, Body: {}",
                methodKey, status, responseBody);

        return switch (status) {
            case BAD_REQUEST -> new IllegalArgumentException("Invalid request: " + responseBody);
            case UNAUTHORIZED -> new SecurityException("Unauthorized access");
            case FORBIDDEN -> new AccessDeniedException("Access forbidden");
            case NOT_FOUND -> new RepositoryNotFoundException(response.request().url());
            case INTERNAL_SERVER_ERROR -> new RuntimeException("Internal server error");
            case SERVICE_UNAVAILABLE -> new RetryableException(response.status(),
                            "Service unavailable",
                            null,
                            (Long) null,
                            response.request());
            default -> new Exception("Unexpected error: " + responseBody);
        };
    }

    private String extractResponseBody(Response response) {
        if (response.body() == null) {
            return "No response body";
        }
        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            log.error("Failed to read response body", ex);
            return "Error reading response body";
        }
    }
}
