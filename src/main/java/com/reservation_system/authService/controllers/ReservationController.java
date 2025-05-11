package com.reservation_system.authService.controllers;

import com.reservation_system.authService.Services.ReservationService;
import com.reservation_system.authService.models.Notification;
import com.reservation_system.authService.models.Reservation;
import com.reservation_system.authService.models.Status;
import com.reservation_system.authService.payload.request.ReservationRequest;
import com.reservation_system.authService.payload.response.MessageResponse;
import com.reservation_system.authService.payload.response.TimeSlotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    // Update reservation status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Notification> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    // Create new reservation
    @PostMapping
    public ResponseEntity<MessageResponse> createReservation(@RequestBody ReservationRequest reservation) {
        return reservationService.createReservation(reservation);
    }

    // Get all reservations with optional date range filter
    @GetMapping
    public List<Reservation> getAllReservations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reservationService.getAllReservations(startDate, endDate);
    }

    // Get all confirmed reservations (for admin view)
    @GetMapping("/confirmed")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllConfirmedReservations() {
        return reservationService.getAllConfirmedReservations();
    }

    // Get reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update reservation
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    // Delete reservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }

    // Get reservations by room
    @GetMapping("/room/{roomId}")
    public List<Reservation> getReservationsByRoomId(@PathVariable Long roomId) {
        return reservationService.getReservationsByRoomId(roomId);
    }

    // Get reservations by user
    @GetMapping("/user/{userId}")
    public List<Reservation> getReservationsByUserId(@PathVariable Long userId) {
        return reservationService.getReservationsByUserId(userId);
    }

    // Get reservations by status
    @GetMapping("/status/{status}")
    public List<Reservation> getReservationsByStatus(@PathVariable Status status) {
        return reservationService.getReservationsByStatus(status);
    }

    // Check available time slots for a room on a specific date
    @GetMapping("/available-slots")
    public List<TimeSlotResponse> getAvailableTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long roomId) {
        return reservationService.getAvailableTimeSlots(date, roomId);
    }

    // Get reservations for a specific date and room
    @GetMapping("/date/{date}/room/{roomId}")
    public List<Reservation> getReservationsByDateAndRoomId(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Long roomId) {
        return reservationService.getReservationsByDateAndRoomId(date, roomId);
    }

    // Get reservations for a date range and room
    @GetMapping("/date-range/room/{roomId}")
    public List<Reservation> getReservationsByDateRangeAndRoomId(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable Long roomId) {
        return reservationService.getReservationsByDateRangeAndRoomId(startDate, endDate, roomId);
    }
}