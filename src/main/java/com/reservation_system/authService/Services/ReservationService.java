package com.reservation_system.authService.Services;

import com.reservation_system.authService.models.*;
import com.reservation_system.authService.payload.request.ReservationRequest;
import com.reservation_system.authService.payload.response.MessageResponse;
import com.reservation_system.authService.payload.response.TimeSlotResponse;
import com.reservation_system.authService.repository.ReservationRepository;
import com.reservation_system.authService.repository.RoomRepository;
import com.reservation_system.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReservationService {
    private static final LocalTime BUSINESS_HOURS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_HOURS_END = LocalTime.of(21, 0);

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

        LocalDate reservationDate = LocalDate.parse(request.getDate());
        LocalTime fromTime = request.isAllDay() ? BUSINESS_HOURS_START : LocalTime.parse(request.getFromtime());
        LocalTime toTime = request.isAllDay() ? BUSINESS_HOURS_END : LocalTime.parse(request.getTotime());

        // Check if time slot is available
        if (!isTimeSlotAvailable(reservationDate, fromTime, toTime, request.getRoomId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: The selected time slot is not available"));
        }

        // If both exist, create the reservation
        Reservation reservation = new Reservation();
        reservation.setDate(reservationDate);
        reservation.setFromtime(fromTime);
        reservation.setTotime(toTime);
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
                + " on " + reservation.getDate() + " from " + reservation.getFromtime() + " to " + reservation.getTotime()
                + " from user " + sender.getUsername();

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

        // Create notification to inform user about status change
        User admin = userRepository.findById(1L) // Admin is sender
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        User user = userRepository.findById(reservation.getUserId()) // User is receiver
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reservation.getUserId()));

        String statusMessage = "Your reservation for room " + reservation.getRoom().getName()
                + " on " + reservation.getDate() + " from " + reservation.getFromtime() + " to " + reservation.getTotime()
                + " has been " + status.toString().toLowerCase();

        return notificationService.createReservationNotification(
                updatedReservation,
                admin,
                user,
                statusMessage,
                status
        );
    }

    // Check if a time slot is available
    public boolean isTimeSlotAvailable(LocalDate date, LocalTime fromTime, LocalTime toTime, Long roomId) {
        List<Reservation> existingReservations = reservationRepository.findByDateAndRoomId(date, roomId);

        for (Reservation reservation : existingReservations) {
            if (reservation.getStatus() == Status.CANCELLED) {
                continue; // Skip cancelled reservations
            }

            // Check if requested time slot overlaps with existing reservation
            if (!(toTime.isBefore(reservation.getFromtime()) || fromTime.isAfter(reservation.getTotime()))) {
                return false; // Overlap found
            }
        }

        return true; // No overlaps found
    }

    // Get available time slots for a specific date and room
    public List<TimeSlotResponse> getAvailableTimeSlots(LocalDate date, Long roomId) {
        List<Reservation> existingReservations = reservationRepository.findByDateAndRoomId(date, roomId)
                .stream()
                .filter(r -> r.getStatus() != Status.CANCELLED)
                .collect(Collectors.toList());

        // Generate all possible one-hour time slots from 8:00 AM to 9:00 PM
        List<TimeSlotResponse> allTimeSlots = new ArrayList<>();

        for (int hour = BUSINESS_HOURS_START.getHour(); hour < BUSINESS_HOURS_END.getHour(); hour++) {
            LocalTime start = LocalTime.of(hour, 0);
            LocalTime end = LocalTime.of(hour + 1, 0);

            boolean available = true;

            // Check if this slot overlaps with any existing reservation
            for (Reservation reservation : existingReservations) {
                if (!(end.isBefore(reservation.getFromtime()) || start.isAfter(reservation.getTotime()))) {
                    available = false;
                    break;
                }
            }

            if (available) {
                allTimeSlots.add(new TimeSlotResponse(start.toString(), end.toString(), true));
            }
        }

        return allTimeSlots;
    }

    // Get all reservations with date range filter
    public List<Reservation> getAllReservations(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return reservationRepository.findByDateBetween(startDate, endDate);
        }
        return reservationRepository.findAll();
    }

    // Get all confirmed reservations
    public List<Reservation> getAllConfirmedReservations() {
        return reservationRepository.findByStatus(Status.CONFIRMED);
    }

    // Read (by ID)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    // Update
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + id));

        reservation.setDate(reservationDetails.getDate());
        reservation.setFromtime(reservationDetails.getFromtime());
        reservation.setTotime(reservationDetails.getTotime());
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