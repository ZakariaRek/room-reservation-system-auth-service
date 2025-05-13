package com.reservation_system.authService.controllers;

import com.reservation_system.authService.models.Reservation;
import com.reservation_system.authService.models.Status;
import com.reservation_system.authService.payload.response.DashboardStatsResponse;
import com.reservation_system.authService.payload.response.RoomUtilizationResponse;
import com.reservation_system.authService.payload.response.ReservationsByStatusResponse;
import com.reservation_system.authService.payload.response.ReservationsByDayResponse;
import com.reservation_system.authService.repository.ReservationRepository;
import com.reservation_system.authService.repository.RoomRepository;
import com.reservation_system.authService.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")

public class DashboardController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    // Get overall statistics for the dashboard
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        // Count total rooms
        stats.setTotalRooms(roomRepository.count());

        // Count total reservations
        stats.setTotalReservations(reservationRepository.count());

        // Count total users
        stats.setTotalUsers(userRepository.count());

        // Count total pending reservations
        stats.setPendingReservations(reservationRepository.countByStatus(Status.PENDING));

        // Count total confirmed reservations
        stats.setConfirmedReservations(reservationRepository.countByStatus(Status.CONFIRMED));

        // Count total cancelled reservations
        stats.setCancelledReservations(reservationRepository.countByStatus(Status.CANCELLED));

        // Calculate today's reservations
        LocalDate today = LocalDate.now();
        stats.setTodayReservations(reservationRepository.countByDate(today));

        // Calculate this week's reservations
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        stats.setWeekReservations(reservationRepository.countByDateBetween(startOfWeek, endOfWeek));

        return ResponseEntity.ok(stats);
    }

    // Get reservations by status
    @GetMapping("/reservations-by-status")
    public ResponseEntity<List<ReservationsByStatusResponse>> getReservationsByStatus() {
        List<ReservationsByStatusResponse> result = new ArrayList<>();

        // Count reservations by status
        Long pendingCount = reservationRepository.countByStatus(Status.PENDING);
        Long confirmedCount = reservationRepository.countByStatus(Status.CONFIRMED);
        Long cancelledCount = reservationRepository.countByStatus(Status.CANCELLED);

        result.add(new ReservationsByStatusResponse("Pending", pendingCount));
        result.add(new ReservationsByStatusResponse("Confirmed", confirmedCount));
        result.add(new ReservationsByStatusResponse("Cancelled", cancelledCount));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/room-utilization")
    public ResponseEntity<List<RoomUtilizationResponse>> getRoomUtilization(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to the current month if not specified
        final LocalDate finalStartDate = (startDate == null) ?
                LocalDate.now().withDayOfMonth(1) : startDate;
        final LocalDate finalEndDate = (endDate == null) ?
                LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()) : endDate;

        // Get all reservations for the period
        List<Reservation> reservations = reservationRepository.findByDateBetweenAndStatus(
                finalStartDate, finalEndDate, Status.CONFIRMED);

        // Group reservations by room
        Map<Long, List<Reservation>> reservationsByRoom = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getRoomId));

        List<RoomUtilizationResponse> result = new ArrayList<>();

        // Get all rooms
        roomRepository.findAll().forEach(room -> {
            RoomUtilizationResponse utilization = new RoomUtilizationResponse();
            utilization.setRoomId(room.getId());
            utilization.setRoomName(room.getName());

            // Count reservations for this room
            List<Reservation> roomReservations = reservationsByRoom.getOrDefault(room.getId(), Collections.emptyList());
            utilization.setReservationCount((long) roomReservations.size());

            // Calculate utilization rate (as percentage of days in the period)
            long totalDays = ChronoUnit.DAYS.between(finalStartDate, finalEndDate) + 1;
            long reservedDays = roomReservations.stream()
                    .map(Reservation::getDate)
                    .distinct()
                    .count();

            double utilizationRate = totalDays > 0 ? (double) reservedDays / totalDays * 100 : 0;
            utilization.setUtilizationRate(Math.round(utilizationRate * 100.0) / 100.0); // Round to 2 decimal places

            result.add(utilization);
        });

        // Sort by utilization rate descending
        result.sort(Comparator.comparingDouble(RoomUtilizationResponse::getUtilizationRate).reversed());

        return ResponseEntity.ok(result);
    }

    // Get reservations by day of week
    @GetMapping("/reservations-by-day")
    public ResponseEntity<List<ReservationsByDayResponse>> getReservationsByDay(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to the current month if not specified
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        // Get all reservations for the period
        List<Reservation> reservations = reservationRepository.findByDateBetween(startDate, endDate);

        // Initialize counters for each day of week
        Map<DayOfWeek, Long> reservationsByDayOfWeek = Arrays.stream(DayOfWeek.values())
                .collect(Collectors.toMap(day -> day, day -> 0L));

        // Count reservations by day of week
        reservations.forEach(reservation -> {
            DayOfWeek dayOfWeek = reservation.getDate().getDayOfWeek();
            reservationsByDayOfWeek.put(dayOfWeek, reservationsByDayOfWeek.get(dayOfWeek) + 1);
        });

        // Create result list
        List<ReservationsByDayResponse> result = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            Long count = reservationsByDayOfWeek.get(day);
            result.add(new ReservationsByDayResponse(dayName, count));
        }

        return ResponseEntity.ok(result);
    }

    // Get recent reservations
    @GetMapping("/recent-reservations")
    public ResponseEntity<List<Reservation>> getRecentReservations(
            @RequestParam(defaultValue = "10") int limit) {

        List<Reservation> recentReservations = reservationRepository.findTop10ByOrderByIdDesc();

        // Limit the results if needed
        if (recentReservations.size() > limit) {
            recentReservations = recentReservations.subList(0, limit);
        }

        return ResponseEntity.ok(recentReservations);
    }
}