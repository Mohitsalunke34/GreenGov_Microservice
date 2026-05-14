package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ComplianceRecord;
import com.example.demo.model.Enums.ComplianceResult;
import com.example.demo.model.Enums.ComplianceSubjectType;

public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {


	List<ComplianceRecord> findBySubjectTypeAndSubjectId(ComplianceSubjectType subjectType, Long subjectId);

	long countByResult(ComplianceResult result);
}