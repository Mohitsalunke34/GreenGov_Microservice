package com.example.demo.client;

import com.example.demo.dto.ParticipantStatusResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "participant-service")
public interface ParticipantFeignClient {

    @GetMapping("/api/participants/{participantId}/status")
    ParticipantStatusResponseDto getParticipantStatus(
        @PathVariable("participantId") Long participantId
    );
}