package com.cognizant.greengov.model.register_login;
 
import java.time.LocalDateTime;

import com.cognizant.greengov.model.Enums.DocumentType;
import com.cognizant.greengov.model.Enums.VerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Entity
@Table(name = "documents",
       indexes = {
           @Index(name = "idx_doc_owner", columnList = "owner_user_id"),
           @Index(name = "idx_doc_status", columnList = "verificationStatus")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Who uploaded it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private UserAccount ownerUser;
 
    // Link to the Participant (Citizen/Business) for registration documents
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private ParticipantProfile participant;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentType documentType;
 
    // Location in object storage / file server
    @Column(nullable = false, length = 500)
    private String fileUri;
 
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
 
    @PrePersist
    public void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }
}