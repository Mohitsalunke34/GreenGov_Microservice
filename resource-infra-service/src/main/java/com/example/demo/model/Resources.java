package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resources {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long resourceId;

	@Column(name = "project_id", nullable = false)
	private long projectId;

	@Column(nullable = false)
	private String type;

	private double quantity;

	private String status;
}