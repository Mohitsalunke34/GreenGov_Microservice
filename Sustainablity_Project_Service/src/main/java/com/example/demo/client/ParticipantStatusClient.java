package com.example.demo.client;

import com.example.demo.dto.ParticipantStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipantStatusClient {

    private final ParticipantFeignClient participantFeignClient;

    public boolean isVerified(Long participantId) {

        ParticipantStatusResponseDto response =
            participantFeignClient.getParticipantStatus(participantId);

        if (response == null) {
            throw new IllegalStateException(
                "Unable to fetch participant verification status"
            );
        }

        return "VERIFIED".equalsIgnoreCase(response.getStatus());
    }
}
