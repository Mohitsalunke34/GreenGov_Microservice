package com.cognizant.greengov.dto.disburse_incentive_dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DisbursementCreateRequestDTO {

	@NotNull
	private Long incentiveId;

	@NotNull
	private Double amount;

	@NotNull
	private LocalDate paymentDate;
}