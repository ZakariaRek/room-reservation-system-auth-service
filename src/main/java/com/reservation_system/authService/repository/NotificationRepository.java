package com.reservation_system.authService.repository;

import com.reservation_system.authService.models.Notification;
import com.reservation_system.authService.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByStatus(Status status);

    List<Notification> findByReservationId(Long reservationId);

    List<Notification> findByDateAfter(LocalDateTime date);

    List<Notification> findByUserIdAndStatus(Long userId, Status status);
}