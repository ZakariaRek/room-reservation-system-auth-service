package com.reservation_system.authService.repository;

import com.reservation_system.authService.models.Room;

import com.reservation_system.authService.models.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByType(RoomType type);

    List<Room> findByCapacityGreaterThanEqual(Integer capacity);

    Optional<Room> findByName(String name);

    List<Room> findByUserId(Long userId);
}


