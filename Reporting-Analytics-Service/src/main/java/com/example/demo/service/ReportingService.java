package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.ReportAnalyticsDTO;
import com.example.demo.model.ReportScope;
import com.example.demo.model.Reports;

public interface ReportingService {

    Reports generateReport(ReportScope scope);

    List<Reports> getReportsByScope(ReportScope scope);

    Reports getReportById(Long reportId);

    Map<ReportScope, Reports> getSummaryReports();

    ReportAnalyticsDTO getAnalytics();
}