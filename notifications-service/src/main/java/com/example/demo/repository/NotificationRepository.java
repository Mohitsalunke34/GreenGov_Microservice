package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Page<Notification> findByUserId(Long userId, Pageable pageable);

	List<Notification> findByUserIdAndStatus(Long userId, Notification.Status status);

	@Modifying
	@Query("UPDATE Notification n SET n.status = com.example.demo.model.Notification$Status.READ WHERE n.userId = :userId")
	void markAllAsReadByUserId(@Param("userId") Long userId);
}