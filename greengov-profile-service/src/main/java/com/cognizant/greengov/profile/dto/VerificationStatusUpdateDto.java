package com.cognizant.greengov.profile.dto;

import com.cognizant.greengov.profile.model.VerificationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerificationStatusUpdateDto {
    @NotNull
    private VerificationStatus status;
}