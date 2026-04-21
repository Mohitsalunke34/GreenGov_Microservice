package com.example.demo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "INCENTIVE-SERVICE")
public interface IncentiveClient {

	@GetMapping("/api/incentives/{id}/exists")
	Boolean incentiveExists(@PathVariable Long id);
}
