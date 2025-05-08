package com.reservation_system.authService.Services;

import com.reservation_system.authService.models.Notification;
import com.reservation_system.authService.models.Reservation;
import com.reservation_system.authService.models.Status;
import com.reservation_system.authService.models.User;
import com.reservation_system.authService.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    // Create
    public Notification createNotification(Notification notification) {
        if(notification.getDate() == null) {
            notification.setDate(LocalDateTime.now());
        }
        return notificationRepository.save(notification);
    }

    // Create notification for reservation
    public Notification createReservationNotification(Reservation reservation, User sender, User receiver, String message, Status status) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setDate(LocalDateTime.now());
        notification.setStatus(status);
        notification.setReservation(reservation);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        return notificationRepository.save(notification);
    }

    // Change notification status
    public Notification updateNotificationStatus(Long id, Status status) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
        notification.setStatus(status);
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
        notification.setSender(notificationDetails.getSender());
        notification.setReceiver(notificationDetails.getReceiver());
        return notificationRepository.save(notification);
    }

    // Delete
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public List<Notification> getNotificationsBySenderId(Long senderId) {
        return notificationRepository.findBySenderId(senderId);
    }

    public List<Notification> getNotificationsByReceiverId(Long receiverId) {
        return notificationRepository.findByReceiverId(receiverId);
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

    public List<Notification> getNotificationsByReceiverIdAndStatus(Long receiverId, Status status) {
        return notificationRepository.findByReceiverIdAndStatus(receiverId, status);
    }

    public List<Notification> getNotificationsBySenderIdAndStatus(Long senderId, Status status) {
        return notificationRepository.findBySenderIdAndStatus(senderId, status);
    }
}