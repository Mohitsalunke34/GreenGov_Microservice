// Disbursement
package com.cognizant.greengov.model.incentive_subsidy;

import java.time.LocalDate;

import com.cognizant.greengov.model.register_login.UserAccount;

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
@Table(name = "disbursements", indexes = @Index(name = "idx_disbursement_officer", columnList = "officer_user_id"))
@Data
public class Disbursement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long disbursementId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "incentive_id", nullable = false)
	private Incentive incentive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "officer_user_id", nullable = false)
	private UserAccount officer; // must be an approved officer

	@Column(nullable = false)
	private LocalDate paymentDate;

	@Column(nullable = false)
	private Double amount; // supports partial payouts

	@Column(nullable = false, length = 30)
	private String status; // e.g., INITIATED, SUCCESS, FAILED
}