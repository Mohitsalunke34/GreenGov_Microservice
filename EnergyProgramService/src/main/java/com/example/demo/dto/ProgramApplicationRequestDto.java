package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgramApplicationRequestDto {

    @NotNull(message = "Applicant ID is required")
    private Long applicantId;

    @NotNull(message = "Program ID is required")
    private Long programId;
}