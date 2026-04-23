package com.cognizant.greengov.repository.notification_repo;
 
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognizant.greengov.model.notification.Notification;
 
@Repository

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 1. Paginated search by User ID (Matches Service Implementation)

    Page<Notification> findByUser_Id(Long userId, Pageable pageable);

    // 2. Find by User and specific Enum Status (e.g., Notification.Status.SENT)

    List<Notification> findByUser_IdAndStatus(Long userId, Notification.Status status);
 
    // 3. Bulk Update using the Enum value

    // Note: We use the String name 'READ' here because HQL maps Enums by their name

    @Modifying

    @Query("UPDATE Notification n SET n.status = com.cognizant.greengov.model.notification.Notification$Status.READ WHERE n.user.id = :userId")

    void markAllAsReadByUserId(@Param("userId") Long userId);

}
 