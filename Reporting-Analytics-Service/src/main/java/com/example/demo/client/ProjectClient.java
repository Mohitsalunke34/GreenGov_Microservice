package com.example.demo.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "SUSTAINABILITYPROJECTSERVICE")
public interface ProjectClient {

    @GetMapping("/api/projects/report-metrics")
    Map<String, Object> getProjectReportMetrics();
}
