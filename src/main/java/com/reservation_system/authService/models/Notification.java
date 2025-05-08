package com.reservation_system.authService.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "sender_id", insertable = false, updatable = false)
    private Long senderId;

    @Column(name = "receiver_id", insertable = false, updatable = false)
    private Long receiverId;

    @Column(name = "reservation_id", insertable = false, updatable = false)
    private Long reservationId;


    // Keep the relationship mappings
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @JsonIgnore
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @JsonIgnore

    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    @JsonIgnore// Removed 'unique = true'
    private Reservation reservation;


}