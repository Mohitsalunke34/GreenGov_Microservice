package com.cognizant.greengov.dto.disburse_incentive_dto;

import java.time.LocalDate;

import lombok.Data;

@Data
//@AllArgsConstructor
public class DisbursementResponseDTO {

	
	private Long disbursementId;
	private Long incentiveId;
	private Long officerUserId;

	private Double amount;
	private LocalDate paymentDate;
	private String status;
}