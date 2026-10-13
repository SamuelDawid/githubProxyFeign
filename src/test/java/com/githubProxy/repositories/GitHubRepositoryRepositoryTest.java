package com.githubProxy.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
public class GitHubRepositoryRepositoryTest {
    @Autowired
    private GitHubRepositoriesRepository repository;

    @Autowired
    private TestEntityManager entityManager;


}
