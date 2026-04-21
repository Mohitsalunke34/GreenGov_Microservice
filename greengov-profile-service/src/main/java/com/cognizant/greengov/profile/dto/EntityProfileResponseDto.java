package com.cognizant.greengov.profile.dto;

import java.util.List;

import com.cognizant.greengov.profile.model.EntityType;
import com.cognizant.greengov.profile.model.VerificationStatus;

import lombok.Data;

@Data
public class EntityProfileResponseDto {
    private Long id;
    private String legalName;
    private EntityType entityType;
    private String address;
    private String contactInfo;
    private VerificationStatus status;
    private List<DocumentResponseDto> documents;
}