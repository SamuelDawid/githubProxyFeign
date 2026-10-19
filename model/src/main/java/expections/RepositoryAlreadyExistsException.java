package expections;



public class RepositoryAlreadyExistsException extends GitHubClientException {
    public RepositoryAlreadyExistsException(String owner,String repositoryName) {

        super(owner + " already has repository " + repositoryName, 409);
    }
}
