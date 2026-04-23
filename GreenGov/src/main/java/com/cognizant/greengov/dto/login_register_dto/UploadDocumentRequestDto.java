package com.cognizant.greengov.dto.login_register_dto;
 
import com.cognizant.greengov.model.Enums.DocumentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
 
@Data
public class UploadDocumentRequestDto {
    @NotNull
    private DocumentType documentType;
 
    @NotBlank
    private String fileUri;
}