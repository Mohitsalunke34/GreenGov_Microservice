package com.example.demo.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "COMPLIANCE-AUDIT-SERVICE")
public interface ComplianceClient {

    @GetMapping("/api/compliance/report-metrics")
    Map<String, Object> getComplianceReportMetrics();
}
