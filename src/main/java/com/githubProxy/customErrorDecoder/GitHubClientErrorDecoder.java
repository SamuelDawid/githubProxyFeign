package com.githubProxy.customErrorDecoder;

import com.githubProxy.exceptions.RepositoryNotFoundException;
import com.githubProxy.exceptions.handler.GitHubClientException;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class GitHubClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String responseBody = extractResponseBody(response);
        FeignException feignException = feign.FeignException.errorStatus(methodKey, response);
        log.error("Feign client error. Method: {}, Status: {}, Body: {}",
                methodKey, status, responseBody);

        return switch (status) {
            case NOT_FOUND -> new RepositoryNotFoundException(response.request().url());
            case BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT -> new RetryableException(
                    response.status(),
                    feignException.getMessage(),
                    response.request().httpMethod(),
                    feignException,
                    (Long) null,
                    response.request()
            );
            default -> new GitHubClientException("Unexpected error: " + responseBody, HttpStatus.BAD_GATEWAY);
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
