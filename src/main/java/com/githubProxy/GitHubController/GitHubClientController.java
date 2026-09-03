package com.githubProxy.GitHubController;

import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GitHubClientController {
    private final GitHubClientService service;

    @GetMapping("/repositories/{owner}/{repository-name}")
    public RepositoryDto getByOwnerAndRepositoryName(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName){
       return service.getByOwnerAndRepositoryName(owner,repositoryName);
    }
    @GetMapping("local/repositories/{owner}/{repository-name}")
    public GitHubRepositoryDto getLocalRepositoryByOwnerAndRepositoryName(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName){
        return service.getLocalRepositoryByOwnerAndRepositoryName(owner,repositoryName);
    }
    @PostMapping("/repositories/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.CREATED)
    public GitHubRepositoryDto create(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName){
        return service.create(owner,repositoryName);
    }

}
