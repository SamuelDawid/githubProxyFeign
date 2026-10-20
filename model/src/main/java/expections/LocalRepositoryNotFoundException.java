package expections;


public class LocalRepositoryNotFoundException extends GitHubClientException {
    public LocalRepositoryNotFoundException(String owner, String repositoryName) {
        super("Local repository " + repositoryName + " not found for: " + owner, 404);
    }
}
