package com.example.demo.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ENERGYPROGRAMSERVICE")
public interface EnergyProgramClient {

	@GetMapping("/api/programs/{id}/exists")
	Boolean programExists(@PathVariable Long id);
}
