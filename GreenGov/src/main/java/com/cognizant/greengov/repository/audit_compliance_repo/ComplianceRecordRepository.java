package com.cognizant.greengov.repository.audit_compliance_repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordResponseDTO;
import com.cognizant.greengov.model.Enums.ComplianceSubjectType;
import com.cognizant.greengov.model.compliance_audit.ComplianceRecord;
import com.cognizant.greengov.model.register_login.ParticipantProfile;

public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

	List<ComplianceRecord> findByParticipant(ParticipantProfile participant);

	List<ComplianceRecord> findBySubjectTypeAndSubjectId(ComplianceSubjectType subjectType, Long SubjectId);
}