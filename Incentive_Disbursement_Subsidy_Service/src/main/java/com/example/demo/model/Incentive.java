package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incentives",
       indexes = {
           @Index(name = "idx_incentive_program_beneficiary",
                  columnList = "program_id, beneficiary_id")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_incentive_application",
                             columnNames = { "application_id" })
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incentive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incentiveId;

    @Column(name = "application_id", nullable = false, unique = true)
    private Long applicationId;

    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "beneficiary_id", nullable = false)
    private Long beneficiaryId;

    @Column(name = "approved_by_user_id")
    private Long approvedBy;

    /**
     * Total sanctioned amount
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * Remaining amount to be disbursed
     * ✅ REQUIRED for Disbursement logic
     */
    @Column(name = "remaining_amount", nullable = false)
    private Double remainingAmount;

    @Column(name = "sanctioned_date", nullable = false)
    private LocalDate sanctionedDate;

    @Column(nullable = false, length = 30)
    private String status;

    @OneToMany(mappedBy = "incentive",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @OrderBy("paymentDate ASC")
    private List<Disbursement> disbursements = new ArrayList<>();
}