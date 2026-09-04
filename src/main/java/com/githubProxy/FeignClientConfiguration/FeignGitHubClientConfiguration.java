package com.githubProxy.FeignClientConfiguration;

import com.githubProxy.customErrorDecoder.GitHubClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignGitHubClientConfiguration {
    @Bean
    public ErrorDecoder GitHubErrorDecoder() {
        return new GitHubClientErrorDecoder();
    }
}
