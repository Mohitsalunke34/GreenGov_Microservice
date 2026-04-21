package com.example.demo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.ParticipantBasicDTO;

@FeignClient(name = "PROFILE-SERVICE")
public interface ParticipantClient {

	@GetMapping("/api/participants/{id}/basic")
	ParticipantBasicDTO getParticipant(@PathVariable Long id);
}