package com.cognizant.greengov.profile.dto;

import java.time.LocalDateTime;

import com.cognizant.greengov.profile.model.DocumentType;
import com.cognizant.greengov.profile.model.VerificationStatus;

import lombok.Data;

@Data
public class DocumentResponseDto {
    private Long id;
    private DocumentType documentType;
    private String fileUri;
    private LocalDateTime uploadedDate;
    private VerificationStatus verificationStatus;
}