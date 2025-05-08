package com.reservation_system.authService.controllers;

import com.reservation_system.authService.Services.NotificationService;
import com.reservation_system.authService.models.Notification;
import com.reservation_system.authService.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    // Update notification status endpoint
    @PatchMapping("/{id}/status")
    public ResponseEntity<Notification> updateNotificationStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        return ResponseEntity.ok(notificationService.updateNotificationStatus(id, status));
    }

    // Create
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    // Read (all)
    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    // Read (by ID)
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.updateNotification(id, notification));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    // Updated custom methods for sender/receiver
    @GetMapping("/sender/{senderId}")
    public List<Notification> getNotificationsBySenderId(@PathVariable Long senderId) {
        return notificationService.getNotificationsBySenderId(senderId);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<Notification> getNotificationsByReceiverId(@PathVariable Long receiverId) {
        return notificationService.getNotificationsByReceiverId(receiverId);
    }

    @GetMapping("/status/{status}")
    public List<Notification> getNotificationsByStatus(@PathVariable Status status) {
        return notificationService.getNotificationsByStatus(status);
    }

    @GetMapping("/reservation/{reservationId}")
    public List<Notification> getNotificationsByReservationId(@PathVariable Long reservationId) {
        return notificationService.getNotificationsByReservationId(reservationId);
    }

    @GetMapping("/after-date")
    public List<Notification> getNotificationsAfterDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return notificationService.getNotificationsAfterDate(date);
    }

    @GetMapping("/receiver/{receiverId}/status/{status}")
    public List<Notification> getNotificationsByReceiverIdAndStatus(
            @PathVariable Long receiverId, @PathVariable Status status) {
        return notificationService.getNotificationsByReceiverIdAndStatus(receiverId, status);
    }

    @GetMapping("/sender/{senderId}/status/{status}")
    public List<Notification> getNotificationsBySenderIdAndStatus(
            @PathVariable Long senderId, @PathVariable Status status) {
        return notificationService.getNotificationsBySenderIdAndStatus(senderId, status);
    }
}