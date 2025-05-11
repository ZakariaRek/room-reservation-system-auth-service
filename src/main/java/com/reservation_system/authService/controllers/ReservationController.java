package com.reservation_system.authService.controllers;

import com.reservation_system.authService.Services.ReservationService;
import com.reservation_system.authService.models.Notification;
import com.reservation_system.authService.models.Reservation;
import com.reservation_system.authService.models.Status;
import com.reservation_system.authService.payload.request.ReservationRequest;
import com.reservation_system.authService.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    // Add endpoint to update reservation status (which will also update notifications)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Notification> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    // Create
    @PostMapping
    public ResponseEntity<MessageResponse> createReservation(@RequestBody ReservationRequest reservation) {
        return reservationService.createReservation(reservation);
    }

    // Read (all)
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // Read (by ID)
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }

    // Custom methods
    @GetMapping("/room/{roomId}")
    public List<Reservation> getReservationsByRoomId(@PathVariable Long roomId) {
        return reservationService.getReservationsByRoomId(roomId);
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getReservationsByUserId(@PathVariable Long userId) {
        return reservationService.getReservationsByUserId(userId);
    }

    @GetMapping("/status/{status}")
    public List<Reservation> getReservationsByStatus(@PathVariable Status status) {
        return reservationService.getReservationsByStatus(status);
    }

    @GetMapping("/date/{date}/room/{roomId}")
    public List<Reservation> getReservationsByDateAndRoomId(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Long roomId) {
        return reservationService.getReservationsByDateAndRoomId(date, roomId);
    }

    @GetMapping("/date-range/room/{roomId}")
    public List<Reservation> getReservationsByDateRangeAndRoomId(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable Long roomId) {
        return reservationService.getReservationsByDateRangeAndRoomId(startDate, endDate, roomId);
    }
}