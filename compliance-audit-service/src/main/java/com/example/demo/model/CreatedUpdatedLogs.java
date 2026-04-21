package com.example.demo.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass // the columns you declare here (e.g., created_at, updated_at, created_by,
					// updated_by) will be copied into the tables of the concrete entities that
					// extend it. You cannot query a @MappedSuperclass directly. it does not create
					// a table on its own also
@Data
@NoArgsConstructor
public abstract class CreatedUpdatedLogs {
	@NotNull // to implement it hibernate validator should also be there in the dependency
	@Column(name = "created_at", updatable = false, nullable = false)
	private Instant createdAt;
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	@Column(name = "created_by", length = 50, nullable = false)
	private String createdBy;
	@Column(name = "updated_by", length = 60, nullable = false)
	private String updatedBy;

	@PrePersist // Only before saving it creates the "created at" field and "updated at" field.
	public void createNew() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	public void eachUpdate() {
		this.updatedAt = Instant.now();
	}

}
