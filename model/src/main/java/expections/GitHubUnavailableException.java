package expections;

public class GitHubUnavailableException extends GitHubClientException {
    private final Throwable cause;

    public GitHubUnavailableException(String message, Throwable cause) {
        super(message, 503);
        this.cause = cause;
    }
}
