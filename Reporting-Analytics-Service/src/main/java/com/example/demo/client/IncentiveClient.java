package com.example.demo.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "INCENTIVE-SERVICE")
public interface IncentiveClient {

    @GetMapping("/api/incentives/report-metrics")
    Map<String, Object> getIncentiveReportMetrics();
}
