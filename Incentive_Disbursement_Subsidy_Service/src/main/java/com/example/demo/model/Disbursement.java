package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(
    name = "disbursements",
    indexes = {
        @Index(
            name = "idx_disbursement_incentive",
            columnList = "incentive_id"
        ),
        @Index(
            name = "idx_disbursement_officer",
            columnList = "officer_user_id"
        )
    }
)
@Data
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disbursementId;

    /**
     * Incentive belongs to SAME microservice.
     * This relationship is allowed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incentive_id", nullable = false)
    private Incentive incentive;

    /**
     * Officer belongs to USER / AUTH service.
     * Store ONLY the officer user ID.
     */
    @Column(name = "officer_user_id", nullable = false)
    private Long officerUserId;

    /**
     * Date on which payment is attempted / completed.
     */
    @Column(nullable = false)
    private LocalDate paymentDate;

    /**
     * Amount disbursed.
     * Supports PARTIAL payouts.
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * INITIATED, SUCCESS, FAILED
     */
    @Column(nullable = false, length = 30)
    private String status;
}
