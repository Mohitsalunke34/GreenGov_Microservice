package com.example.demo.dto;


import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DisbursementProcessResponse {

    private DisbursementResponseDTO disbursement;
    private BudgetSummaryDTO budgetSummary;
    private List<DisbursementResponseDTO> history;

    public DisbursementProcessResponse(
            DisbursementResponseDTO disbursement,
            BudgetSummaryDTO budgetSummary,
            List<DisbursementResponseDTO> history) {
        this.disbursement = disbursement;
        this.budgetSummary = budgetSummary;
        this.history = history;
    }
}
