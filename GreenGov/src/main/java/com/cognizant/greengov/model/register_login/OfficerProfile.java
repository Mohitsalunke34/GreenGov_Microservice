// OfficerProfile (admin-approved)
package com.cognizant.greengov.model.register_login;

import java.time.LocalDateTime;

import com.cognizant.greengov.model.Enums.OfficerType;
import com.cognizant.greengov.model.Enums.ProfileStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Officer profile which needs Admin validation by default the status is pending
@Entity
@Table(name = "officer_profiles", indexes = @Index(name = "idx_officer_status", columnList = "status"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OfficerProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40)
    private OfficerType officerType;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private ProfileStatus status = ProfileStatus.PENDING;

    @Column(length = 120) private String department;
    @Column(length = 120) private String designation;
    @Column(length = 60) private String officeCode;

    private LocalDateTime submittedAt; private LocalDateTime approvedAt;

    @PrePersist public void onCreate() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }
}