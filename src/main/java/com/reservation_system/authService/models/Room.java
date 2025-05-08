package com.reservation_system.authService.models;

import com.reservation_system.authService.models.Equipment;
import com.reservation_system.authService.models.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer capacity;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipment> equipments;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(name = "user_id")
    private Long userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" ,insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
}