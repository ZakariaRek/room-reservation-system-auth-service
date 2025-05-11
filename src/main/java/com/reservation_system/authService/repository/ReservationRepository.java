package com.reservation_system.authService.repository;

import com.reservation_system.authService.models.Reservation;
import com.reservation_system.authService.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByStatus(Status status);

    List<Reservation> findByDateAndRoomId(LocalDate date, Long roomId);

    List<Reservation> findByDateBetweenAndRoomId(LocalDate startDate, LocalDate endDate, Long roomId);

    List<Reservation> findByDateBetween(LocalDate startDate, LocalDate endDate);

    Long countByStatus(Status status);
    Long countByDate(LocalDate date);
    Long countByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Reservation> findByDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, Status status);
    List<Reservation> findTop10ByOrderByIdDesc();

}