// ParticipantProfile (citizen/business)
//It gets created automatically with successful registration,
//ParticipantProfile -> used for programs, incentives, compliance
package com.cognizant.greengov.model.register_login;
 
import java.util.ArrayList;
import java.util.List;

import com.cognizant.greengov.model.Enums.EntityType;
import com.cognizant.greengov.model.Enums.VerificationStatus;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
// For citizen and business only.
@Entity
@Table(name = "participant_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // who applied the program ?
 
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private UserAccount user;
 
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private EntityType entityType; // CITIZEN or BUSINESS

	@OneToMany(mappedBy = "beneficiary", fetch = FetchType.LAZY)
	private List<Incentive> incentives = new ArrayList<>();
	@OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Document> documents = new ArrayList<>();
	@Column(nullable = false, length = 200)
	private String legalName;
	@Column(columnDefinition = "TEXT")
	private String address;
	@Column(columnDefinition = "TEXT")
	private String contactInfoJson; // Your 'contactInfo' mapped to team's naming
}