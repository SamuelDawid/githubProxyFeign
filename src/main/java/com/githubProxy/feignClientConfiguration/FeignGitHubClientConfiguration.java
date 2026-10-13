package com.githubProxy.feignClientConfiguration;

import com.githubProxy.customErrorDecoder.GitHubClientErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class FeignGitHubClientConfiguration {
    @Bean
    public ErrorDecoder gitHubErrorDecoder() {
        return new GitHubClientErrorDecoder();
    }

    @Bean
    public Retryer retryer(){
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(2L),3);
    }
}
