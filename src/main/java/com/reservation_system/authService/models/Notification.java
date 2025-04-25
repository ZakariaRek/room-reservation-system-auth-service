package com.reservation_system.authService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}