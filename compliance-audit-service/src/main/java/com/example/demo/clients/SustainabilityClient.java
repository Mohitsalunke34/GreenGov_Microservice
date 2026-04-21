package com.example.demo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SUSTAINABILITY-SERVICE")
public interface SustainabilityClient {

	@GetMapping("/api/projects/{id}/exists")
	Boolean projectExists(@PathVariable Long id);

	@GetMapping("/api/programs/{id}/exists")
	Boolean programExists(@PathVariable Long id);

}
