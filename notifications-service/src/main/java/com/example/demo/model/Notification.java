package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	private Long entityId;

	@Column(nullable = false, length = 500)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	private LocalDateTime createdDate;

	public enum Category {
	    SCHEME, 
	    SUBSIDY, 
	    PROJECT, 
	    COMPLIANCE, 
	    RESOURCE,           // General
	    INFRASTRUCTURE,     // General
	    RESOURCE_ALLOCATION, // Added
	    RESOURCE_UPDATE,     // Added
	    RESOURCE_DELETE,     // Added
	    STATUS_CHANGE        // Added
	}

	public enum Status {
		SENT, READ, ARCHIVED
	}

	@PrePersist
	protected void onCreate() {
		this.createdDate = LocalDateTime.now();
		if (this.status == null) {
			this.status = Status.SENT;
		}
	}
}