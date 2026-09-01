package com.githubProxy.GitHubController;

import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repos")
public class GitHubClientController {
    private final GitHubClientService service;

    @GetMapping("/{owner}/{repository-name}")
    public RepositoryDto getByOwnerAndRepositoryName(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName){
       return service.getByOwnerAndRepositoryName(owner,repositoryName);
    }
}
