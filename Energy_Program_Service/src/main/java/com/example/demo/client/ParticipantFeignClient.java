package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.ParticipantStatusResponseDto;

@FeignClient(name = "GREENGOV-PROFILE-SERVICE")
public interface ParticipantFeignClient {

	@GetMapping("/api/participants/{id}")
	ParticipantStatusResponseDto getParticipantDetails(@PathVariable("id") Long participantId);
}
