package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient  // Enables registration with the Eureka Server
@EnableFeignClients    // Enables scanning and creation of your Feign Client beans
public class ResourceInfraServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourceInfraServiceApplication.class, args);
	}

}