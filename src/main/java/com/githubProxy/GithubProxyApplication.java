package com.githubProxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GithubProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubProxyApplication.class, args);
	}

}
