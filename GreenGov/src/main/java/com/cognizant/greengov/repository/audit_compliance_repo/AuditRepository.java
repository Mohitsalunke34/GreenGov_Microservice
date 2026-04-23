package com.cognizant.greengov.repository.audit_compliance_repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.Enums.AuditStatus;
import com.cognizant.greengov.model.compliance_audit.Audit;
import com.cognizant.greengov.model.register_login.UserAccount;

public interface AuditRepository extends JpaRepository<Audit, Long> {
	List<Audit> findByOfficer(UserAccount officer);

	List<Audit> findByComplianceRecordId(Long complianceId);

	List<Audit> findByStatus(AuditStatus status);

}
