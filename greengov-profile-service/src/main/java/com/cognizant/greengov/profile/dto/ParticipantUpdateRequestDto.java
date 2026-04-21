package com.cognizant.greengov.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParticipantUpdateRequestDto {
    @NotBlank
    private String legalName;
    
    @NotBlank
    private String address;
    
    @NotBlank
    private String contactInfo;
}