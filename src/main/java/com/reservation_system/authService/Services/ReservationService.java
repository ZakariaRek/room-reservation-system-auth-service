package com.reservation_system.authService.Services;

import com.reservation_system.authService.models.Reservation;
import com.reservation_system.authService.models.Status;
import com.reservation_system.authService.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    // Create
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
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
