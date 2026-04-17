package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // This creates a constructor for all fields
public class BudgetSummaryDTO {
	private Long programId;
	private BigDecimal baseBudget; // From EnergyProgram
	private BigDecimal totalDisbursedSoFar; // The "Amount" just paid
	private BigDecimal remainingIncentive; // Money left for this user
	private BigDecimal remainingProgramBudget;
 
	public void setTotalDisbursedSoFar(BigDecimal totalDisbursedSoFar) {
		this.totalDisbursedSoFar = totalDisbursedSoFar;
	}
 
}