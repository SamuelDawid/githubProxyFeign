package expections;



public class RepositoryNotFoundException extends GitHubClientException {
    public RepositoryNotFoundException(String url) {
        super("Repository not found: " + url, 404);
    }
}
