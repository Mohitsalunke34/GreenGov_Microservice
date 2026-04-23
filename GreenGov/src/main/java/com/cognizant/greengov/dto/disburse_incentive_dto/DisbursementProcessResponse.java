package com.cognizant.greengov.dto.disburse_incentive_dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DisbursementProcessResponse {
	private DisbursementResponseDTO disbursement;
	private BudgetSummaryDTO budgetSummary;
	private List<DisbursementResponseDTO> history; // Added this field

	// Updated constructor to take 3 arguments
	public DisbursementProcessResponse(DisbursementResponseDTO disbursement, BudgetSummaryDTO budgetSummary,
			List<DisbursementResponseDTO> history) {
		this.disbursement = disbursement;
		this.budgetSummary = budgetSummary;
		this.history = history;
	}
}