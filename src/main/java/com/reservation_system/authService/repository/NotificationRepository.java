package com.reservation_system.authService.repository;

import com.reservation_system.authService.models.Notification;
import com.reservation_system.authService.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findBySenderId(Long senderId);

    List<Notification> findByReceiverId(Long receiverId);

    List<Notification> findByStatus(Status status);

    List<Notification> findByReservationId(Long reservationId);

    List<Notification> findByDateAfter(LocalDateTime date);

    List<Notification> findByReceiverIdAndStatus(Long receiverId, Status status);

    List<Notification> findBySenderIdAndStatus(Long senderId, Status status);
}