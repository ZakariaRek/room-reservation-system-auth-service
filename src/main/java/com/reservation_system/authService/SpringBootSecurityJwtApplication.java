package com.reservation_system.authService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringBootSecurityJwtApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
	}
}
