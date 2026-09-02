package com.githubProxy.GitHubController;

import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repos")
public class GitHubClientController {
    private final GitHubClientService service;

    @GetMapping("/{owner}/{repository-name}")
    public RepositoryDto getByOwnerAndRepositoryName(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName){
       return service.getByOwnerAndRepositoryName(owner,repositoryName);
    }

    @PostMapping("/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.CREATED)
    public GitHubRepositoryDto create(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName){
        return service.create(owner,repositoryName);
    }
}
