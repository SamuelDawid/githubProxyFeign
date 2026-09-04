package com.githubProxy.GitHubController;

import com.githubProxy.GitHubService.GitHubClientService;
import com.githubProxy.dto.ErrorMessageDto;
import com.githubProxy.dto.GitHubRepositoryPutCommand;
import com.githubProxy.dto.RepositoryDto;
import com.githubProxy.dto.gitHubRepositoryEntity.GitHubRepositoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Repositories", description = "Manage GitHub-backed and locally stored repositories")
@RestController
@RequiredArgsConstructor
public class GitHubClientController {
    private final GitHubClientService service;
    @Operation(summary = "Get a repository's live details from GitHub")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repository found",
                    content = @Content(schema = @Schema(implementation = RepositoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "GitHub upstream error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/repositories/{owner}/{repository-name}")
    public RepositoryDto getByOwnerAndRepositoryName(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        return service.getByOwnerAndRepositoryName(owner, repositoryName);
    }
    @Operation(summary = "Get a locally saved repository details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repository found",
                    content = @Content(schema = @Schema(implementation = GitHubRepositoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
    })
    @GetMapping("local/repositories/{owner}/{repository-name}")
    public GitHubRepositoryDto getLocalRepositoryByOwnerAndRepositoryName(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        return service.getLocalRepositoryByOwnerAndRepositoryName(owner, repositoryName);
    }

    @Operation(summary = "Create a local repository record")
    @ApiResponse(responseCode = "201", description = "Repository created",
            content = @Content(schema = @Schema(implementation = GitHubRepositoryDto.class)))
    @PostMapping("/repositories/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.CREATED)
    public GitHubRepositoryDto create(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        return service.create(owner, repositoryName);
    }
    @Operation(summary = "Update details of locally saved repository")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Repository updated",
            content = @Content(schema = @Schema(implementation = GitHubRepositoryDto.class))),
            @ApiResponse(responseCode = "404",description = "Repository not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping("/repositories/{owner}/{repository-name}")
    public GitHubRepositoryDto update(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName, @RequestBody GitHubRepositoryPutCommand command) {
        return service.update(owner, repositoryName, command);
    }
    @Operation(summary = "Remove local repository")
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Removed successfully"),
            @ApiResponse(responseCode = "404",description = "Repository not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/repositories/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        service.delete(owner, repositoryName);
    }
}
