package com.cognizant.greengov.dto.sustainability_dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgramApplicationRequestDTO {

    @NotNull
    private Long programId;
}