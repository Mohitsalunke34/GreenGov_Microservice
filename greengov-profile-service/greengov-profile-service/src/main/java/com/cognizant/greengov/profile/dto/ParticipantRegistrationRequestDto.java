package com.cognizant.greengov.profile.dto;

import com.cognizant.greengov.profile.model.EntityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParticipantRegistrationRequestDto {
    @NotNull
    private Long userId;
    
    @NotNull
    private EntityType entityType;
    
    @NotBlank
    private String legalName;
    
    @NotBlank
    private String address;
    
    @NotBlank
    private String contactInfo;
}