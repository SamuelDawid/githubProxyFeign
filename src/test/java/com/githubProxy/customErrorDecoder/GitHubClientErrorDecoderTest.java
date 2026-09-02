package com.githubProxy.customErrorDecoder;

import com.githubProxy.exceptions.RepositoryNotFoundException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GitHubClientErrorDecoderTest {

    private GitHubClientErrorDecoder decoder;

    @BeforeEach
    void  setUp(){
        this.decoder = new GitHubClientErrorDecoder();
    }

    private Response responseWithStatus(int status , Request request, String reason){
            return Response.builder()
                    .status(status)
                    .reason(reason)
                    .request(request)
                    .headers(Map.of())
                    .build();
    }

    @Test
    void decode_When404_ShouldReturnRepositoryNotFoundException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "https://api.github.com/repos/owner/repo",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        Response response = responseWithStatus(404,request,"Not Found");

        Exception result = decoder.decode("GitHubClient#getRepo(String,String)",response);
        assertInstanceOf(RepositoryNotFoundException.class,result);
    }
}