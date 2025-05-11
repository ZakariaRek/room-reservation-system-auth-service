package com.reservation_system.authService.Services;

import com.reservation_system.authService.models.*;
import com.reservation_system.authService.payload.request.ReservationRequest;
import com.reservation_system.authService.payload.response.MessageResponse;
import com.reservation_system.authService.repository.ReservationRepository;
import com.reservation_system.authService.repository.RoomRepository;
import com.reservation_system.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    // Create with notification
    public ResponseEntity<MessageResponse> createReservation(ReservationRequest request) {
        // Set initial status if not set
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (!userOptional.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User with ID " + request.getUserId() + " not found"));
        }

        // Then check if the room exists
        Optional<Room> roomOptional = roomRepository.findById(request.getRoomId());
        if (!roomOptional.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Room with ID " + request.getRoomId() + " not found"));
        }

        // If both exist, create the reservation
        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.parse(request.getDate()));
        reservation.setTime(LocalTime.parse(request.getTime()));
        reservation.setUserId(request.getUserId());
        reservation.setRoomId(request.getRoomId());
        reservation.setStatus(Status.PENDING); // Or whatever default status you want

        reservationRepository.save(reservation);
        User sender = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reservation.getUserId()));

        // Get the admin user as receiver (assuming admin has ID 1, adjust as needed)
        // In a real system, you might want to get all users with ADMIN role
        User receiver = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // Create notification for the new reservation
        String message = "New reservation request for room " + roomOptional.get().getName()
                + " on " + reservation.getDate() + " at " + reservation.getTime() + " from user " + sender.getUsername();

        notificationService.createReservationNotification(
                reservation,
                sender,
                receiver,
                message,
                Status.PENDING
        );
        return ResponseEntity.ok(new MessageResponse("Reservation created successfully"));
    }

    // Update reservation status and related notification
    public Notification updateReservationStatus(Long id, Status status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + id));

        // Update reservation status
        reservation.setStatus(status);
        Reservation updatedReservation = reservationRepository.save(reservation);

        // REMOVE THIS SECTION - Don't update existing notifications
        // List<Notification> notifications = notificationService.getNotificationsByReservationId(id);
        // for (Notification notification : notifications) {
        //    notification.setStatus(status);
        //    notificationService.updateNotification(notification.getId(), notification);
        // }

        // Create notification to inform user about status change
        User admin = userRepository.findById(1L) // Admin is sender
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        User user = userRepository.findById(reservation.getUserId()) // User is receiver
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reservation.getUserId()));

        String statusMessage = "Your reservation for room " + reservation.getRoom().getName()
                + " on " + reservation.getDate() + " has been " + status.toString().toLowerCase();



        return notificationService.createReservationNotification(
                updatedReservation,
                admin,
                user,
                statusMessage,
                status
        );
    }
    // Read (all)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Read (by ID)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    // Update
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID : " + id));
        reservation.setDate(reservationDetails.getDate());
        reservation.setTime(reservationDetails.getTime());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setRoomId(reservationDetails.getRoomId());
        reservation.setUserId(reservationDetails.getUserId());
        return reservationRepository.save(reservation);
    }

    // Delete
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getReservationsByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByStatus(Status status) {
        return reservationRepository.findByStatus(status);
    }

    public List<Reservation> getReservationsByDateAndRoomId(LocalDate date, Long roomId) {
        return reservationRepository.findByDateAndRoomId(date, roomId);
    }

    public List<Reservation> getReservationsByDateRangeAndRoomId(LocalDate startDate, LocalDate endDate, Long roomId) {
        return reservationRepository.findByDateBetweenAndRoomId(startDate, endDate, roomId);
    }
}