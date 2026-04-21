package com.example.demo.dto;


import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DisbursementCreateRequestDTO {

    @NotNull
    private Long incentiveId;

    @NotNull
    private Double amount;

 
    private LocalDate paymentDate;
}