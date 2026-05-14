package com.example.demo.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dto.ParticipantBasicDTO;

@FeignClient(name = "GREENGOV-PROFILE-SERVICE")
public interface ParticipantClient {

	@GetMapping("/api/participants/lookup")
	List<ParticipantBasicDTO> getParticipants();
}