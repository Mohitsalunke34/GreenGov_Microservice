package com.cognizant.greengov.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.compliance_audit.ActivityTracker;

public interface ActivityTrackerRepository extends JpaRepository<ActivityTracker, Long> {
//	Get ALL activity records for a specific user, and show the newest ones first.
//	userId = whose activity you want
//	OrderByTimestampDesc = sort by time, newest to oldest
	List<ActivityTracker> findByUserIdOrderByTimestampDesc(Long userId);

//	Get a user’s activity only within a specific time range, newest first
	List<ActivityTracker> findByUserIdAndTimestampBetweenOrderByTimestampDesc(Long userId, Instant from, Instant to);

//	Get activity related to a specific resource (like a project, task, ticket, file, etc.), newest first.
	List<ActivityTracker> findByResourceTypeAndResourceIdOrderByTimestampDesc(String resourceType, Long resourceId);
}
