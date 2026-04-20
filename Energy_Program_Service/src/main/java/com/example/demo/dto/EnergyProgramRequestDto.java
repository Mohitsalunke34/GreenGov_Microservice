package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnergyProgramRequestDto {

    @NotBlank(message = "Program title must not be blank")
    private String title;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.0", inclusive = false,
                message = "Budget must be greater than 0")
    private BigDecimal budget;

    @NotBlank(message = "Program status must not be blank")
    private String status;
}
