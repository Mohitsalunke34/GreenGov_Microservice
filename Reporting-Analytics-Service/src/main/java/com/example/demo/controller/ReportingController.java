package com.example.demo.controller;



import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReportAnalyticsDTO;
import com.example.demo.model.ReportScope;
import com.example.demo.model.Reports;
import com.example.demo.service.ReportingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @PostMapping("/generate/{scope}")
    public ResponseEntity<Reports> generate(@PathVariable ReportScope scope) {
        return ResponseEntity.ok(reportingService.generateReport(scope));
    }

    @GetMapping("/scope/{scope}")
    public ResponseEntity<List<Reports>> getByScope(@PathVariable ReportScope scope) {
        return ResponseEntity.ok(reportingService.getReportsByScope(scope));
    }

    @GetMapping("/fetchById/{id}")
    public ResponseEntity<Reports> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reportingService.getReportById(id));
    }

    @GetMapping("/fetchBySummary/summary")
    public ResponseEntity<Map<ReportScope, Reports>> getSummary() {
        return ResponseEntity.ok(reportingService.getSummaryReports());
    }

    @GetMapping("/analytics")
    public ResponseEntity<ReportAnalyticsDTO> getAnalytics() {
        return ResponseEntity.ok(reportingService.getAnalytics());
    }
}