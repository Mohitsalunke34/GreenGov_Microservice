package com.cognizant.greengov.dto.disburse_incentive_dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncentiveCreateRequestDTO {

	@NotNull
	private Long applicationId;

	@NotNull
	private Double amount;
}