package com.githubProxy.gitHubClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "githubClient", url = "${app.githubClient.url}",configuration = {})
public interface GitHubClient {
    @GetMapping("/{owner}/{repository-name}")
     GitHubResponse getByOwnerAndRepositoryName(@PathVariable("owner") String owner,@PathVariable("repository-name") String repositoryName);
}
