package com.githubproxy.adapter;

import com.githubProxy.GitHubClientServiceImp;
import com.githubProxy.GitHubClientPort;
import model.GitHubClientService;
import com.githubProxy.GitHubRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {
    @Bean
    GitHubClientService gitHubClientService(
            GitHubRepositoryPort repositoryPort,
            GitHubClientPort clientPort){
        return new GitHubClientServiceImp(repositoryPort,clientPort);
    }
}
