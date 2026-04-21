package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Audit;
import com.example.demo.model.Enums.AuditStatus;

public interface AuditRepository extends JpaRepository<Audit, Long> {

	List<Audit> findByOfficerUserId(Long officerUserId);

	List<Audit> findByComplianceId(Long complianceId);

	List<Audit> findByStatus(AuditStatus status);
}