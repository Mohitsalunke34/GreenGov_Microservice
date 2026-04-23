package com.cognizant.greengov.controller.compliance_audit_controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.complianceauditdto.AuditCreateRequestDTO;
import com.cognizant.greengov.dto.complianceauditdto.AuditResponseDTO;
import com.cognizant.greengov.model.Enums.AuditStatus;
import com.cognizant.greengov.service.compliance_audit_service.AuditService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/audits")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    // create Audit on compliance Id
    @PostMapping
    public ResponseEntity<AuditResponseDTO> start(
            @RequestParam Long auditorUserId,
            @RequestBody @Valid AuditCreateRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.startAudit(dto, auditorUserId));
    }

    // close audit
    @PostMapping("/{auditId}/close")
    public ResponseEntity<AuditResponseDTO> close(
            @PathVariable Long auditId,
            @RequestParam AuditStatus status) {

        return ResponseEntity.ok(service.closeAudit(auditId, status));
    }

    // get all audits performed by an audit manager
    @GetMapping("/by-officer/{officerId}")
    public ResponseEntity<List<AuditResponseDTO>> getByOfficer(
            @PathVariable Long officerId) {

        return ResponseEntity.ok(service.getAuditsByOfficer(officerId));
    }

    // get all audits available for a compliance
    @GetMapping("/by-compliance/{complianceId}")
    public ResponseEntity<List<AuditResponseDTO>> getByCompliance(
            @PathVariable Long complianceId) {

        return ResponseEntity.ok(service.getAuditsByCompliance(complianceId));
    }

    // get all audits by status
    @GetMapping
    public ResponseEntity<List<AuditResponseDTO>> getByStatus(
            @RequestParam AuditStatus status) {

        return ResponseEntity.ok(service.getAuditsByStatus(status));
    }
}