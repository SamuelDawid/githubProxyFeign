package expections;

import lombok.Getter;
@Getter
public class GitHubClientException extends RuntimeException {
    private final int status;
    public GitHubClientException(String message,int status) {
        super(message);
        this.status = status;
    }
}
