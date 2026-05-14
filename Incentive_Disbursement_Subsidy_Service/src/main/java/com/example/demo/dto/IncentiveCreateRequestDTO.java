package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncentiveCreateRequestDTO {

	@NotNull(message = "Participant ID is required")
	private Long participantId;

//    @NotNull(message = "Program ID is required")
	private Long programId;

//    @NotNull(message = "Beneficiary ID is required")
	private Long beneficiaryId;

	@NotNull(message = "Amount is required")
	@Min(value = 0, message = "Amount must be positive")
	private Double amount;
}