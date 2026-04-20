package com.example.demo.dto;

import lombok.Data;

@Data
public class ParticipantStatusResponseDto {
    private Long id;
    private String status; // VERIFIED / PENDING / REJECTED
}
