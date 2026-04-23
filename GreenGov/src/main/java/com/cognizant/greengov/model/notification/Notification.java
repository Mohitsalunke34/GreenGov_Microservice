package com.cognizant.greengov.model.notification;
 
import java.time.LocalDateTime;

import com.cognizant.greengov.model.register_login.UserAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount user;
 
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
		SCHEME, SUBSIDY, PROJECT, COMPLIANCE
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