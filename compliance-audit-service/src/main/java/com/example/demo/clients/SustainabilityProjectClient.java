package com.example.demo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SUSTAINABILITYPROJECTSERVICE")
public interface SustainabilityProjectClient {

	@GetMapping("/api/projects/{id}/exists")
	Boolean projectExists(@PathVariable Long id);
}
