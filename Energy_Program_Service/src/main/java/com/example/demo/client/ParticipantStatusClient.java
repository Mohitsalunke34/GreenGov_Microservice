package com.example.demo.client;

import org.springframework.stereotype.Component;

import com.example.demo.dto.ParticipantStatusResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ParticipantStatusClient {

	private final ParticipantFeignClient participantFeignClient;

	public boolean isVerified(Long participantId) {

		ParticipantStatusResponseDto participant = participantFeignClient.getParticipantDetails(participantId);

		if (participant == null) {
			throw new IllegalStateException("No participant details found for id: " + participantId);
		}

		return "VERIFIED".equalsIgnoreCase(participant.getStatus());
	}
}