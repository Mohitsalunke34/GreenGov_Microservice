package com.cognizant.greengov.repository.reports_repo;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.Enums.ReportScope;
import com.cognizant.greengov.model.Enums.ReportStatus;
import com.cognizant.greengov.model.compliance_audit.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

	// Optional query types (JPA will generate automatically)
	List<Report> findByScope(ReportScope scope);

	List<Report> findByStatus(ReportStatus status);

	List<Report> findByGeneratedDateBetween(Instant start, Instant end);

	// If you want direct filtering by relations
	List<Report> findByProject(Long projectId);

	List<Report> findByProgram(Long programId);

	List<Report> findByIncentive(Long incentiveId);
}