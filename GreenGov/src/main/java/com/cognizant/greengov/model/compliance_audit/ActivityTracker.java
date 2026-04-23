package com.cognizant.greengov.model.compliance_audit;



import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
 
@Entity
@Table(name = "activity_tracker", indexes = { @Index(name = "idx_track_user_time", columnList = "user_id, timestamp"),
		@Index(name = "idx_track_activity", columnList = "resource_type, resource_id") })
public class ActivityTracker {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audit_id")
	private Long id;
 
	@NotNull
	@Column(name = "user_id", nullable = false)
	private Long userId; // Soft FK to User — concrete mapping possible when User is added.
 
	@NotNull
	@Size(min = 2, max = 100)
	@Column(name = "action", nullable = false, length = 100)
	private String action; // e.g., CREATE_COMPLIANCE_RECORD
 
	@NotNull
	@Size(min = 2, max = 50)
	@Column(name = "resource_type", nullable = false, length = 50)
	private String resourceType; // e.g., "ComplianceRecord", "Audit", "Report"
 
	@NotNull
	@Column(name = "resource_id", nullable = false)
	private Long resourceId;
 
	@NotNull
	@Column(name = "timestamp", nullable = false, updatable = false)
	private Instant timestamp;
 
	@Size(max = 45)
	@Column(name = "ip_address", length = 45)
	private String ipAddress;
 
	@Size(max = 200)
	@Column(name = "user_agent", length = 200)
	private String userAgent;
 
	@PrePersist
	protected void prePersist() {
		if (timestamp == null)
			timestamp = Instant.now();
	}
 
	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
 
	public Long getUserId() {
		return userId;
	}
 
	public void setUserId(Long userId) {
		this.userId = userId;
	}
 
	public String getAction() {
		return action;
	}
 
	public void setAction(String action) {
		this.action = action;
	}
 
	public String getResourceType() {
		return resourceType;
	}
 
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
 
	public Long getResourceId() {
		return resourceId;
	}
 
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
 
	public String getIpAddress() {
		return ipAddress;
	}
 
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
 
	public String getUserAgent() {
		return userAgent;
	}
 
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
 
	public ActivityTracker(Long id, @NotNull Long userId, @NotNull @Size(min = 2, max = 100) String action,
			@NotNull @Size(min = 2, max = 50) String resourceType, @NotNull Long resourceId,
			@Size(max = 45) String ipAddress, @Size(max = 200) String userAgent) {
		super();
		this.id = id;
		this.userId = userId;
		this.action = action;
		this.resourceType = resourceType;
		this.resourceId = resourceId;
		this.ipAddress = ipAddress;
		this.userAgent = userAgent;
	}
 
	public ActivityTracker() {
		super();
	}
	
}