// Infrastructure
package com.cognizant.greengov.model.resource_infrastructure;

import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "infrastructure")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Infrastructure {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long infraId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private SustainabilityProject project;

	@Column(nullable = false)
	private String type;
	@Column(nullable = false)
	private String location;
	@Column(nullable = false)
	private int capacity;
	private String status;
}