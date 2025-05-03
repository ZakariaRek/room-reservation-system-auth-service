package com.reservation_system.authService.Services;

import com.reservation_system.authService.models.Notification;
import com.reservation_system.authService.models.Status;
import com.reservation_system.authService.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    protected NotificationRepository notificationRepository;

    // Create
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Read (all)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Read (by ID)
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    // Update
    public Notification updateNotification(Long id, Notification notificationDetails) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
        notification.setMessage(notificationDetails.getMessage());
        notification.setStatus(notificationDetails.getStatus());
        return notificationRepository.save(notification);
    }

    // Delete
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getNotificationsByStatus(Status status) {
        return notificationRepository.findByStatus(status);
    }

    public List<Notification> getNotificationsByReservationId(Long reservationId) {
        return notificationRepository.findByReservationId(reservationId);
    }

    public List<Notification> getNotificationsAfterDate(LocalDateTime date) {
        return notificationRepository.findByDateAfter(date);
    }

    public List<Notification> getNotificationsByUserIdAndStatus(Long userId, Status status) {
        return notificationRepository.findByUserIdAndStatus(userId, status);
    }

}
