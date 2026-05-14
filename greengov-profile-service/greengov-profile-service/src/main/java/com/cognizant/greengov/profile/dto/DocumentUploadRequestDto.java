package com.cognizant.greengov.profile.dto;

import com.cognizant.greengov.profile.model.DocumentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentUploadRequestDto {
    @NotNull
    private DocumentType documentType;
    
    @NotBlank
    private String base64Content;
}