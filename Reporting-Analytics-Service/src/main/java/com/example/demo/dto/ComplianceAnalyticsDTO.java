package com.example.demo.dto;

import lombok.Data;

@Data
public class ComplianceAnalyticsDTO {

    private Integer totalAudits;              // COUNT(*)
    private Integer compliantCount;           // result = COMPLIANT
    private Integer nonCompliantCount;        // result = NON_COMPLIANT
}
