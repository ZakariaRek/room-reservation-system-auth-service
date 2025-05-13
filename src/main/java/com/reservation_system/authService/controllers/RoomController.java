package com.reservation_system.authService.controllers;

import com.reservation_system.authService.models.Equipment;
import com.reservation_system.authService.models.Room;
import com.reservation_system.authService.repository.EquipmentRepository;
import com.reservation_system.authService.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{roomId}/equipments")
    public List<Equipment> getEquipmentsByRoom(@PathVariable Long roomId) {
        return equipmentRepository.findByRoomId(roomId);
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        Room savedRoom = roomRepository.save(room);

        if (room.getEquipments() != null) {
            for (Equipment eq : room.getEquipments()) {
                eq.setRoom(savedRoom); // Associer à la salle
                equipmentRepository.save(eq); // Enregistrer l'équipement
            }
        }

        return savedRoom;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room updatedRoom) {
        Optional<Room> optionalRoom = roomRepository.findById(id);
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Room existingRoom = optionalRoom.get();

        // Mettre à jour les infos de la salle
        existingRoom.setName(updatedRoom.getName());
        existingRoom.setCapacity(updatedRoom.getCapacity());
        existingRoom.setType(updatedRoom.getType());
        existingRoom.setUserId(updatedRoom.getUserId());

        // Supprimer les anciens équipements liés
        equipmentRepository.deleteAll(equipmentRepository.findByRoomId(id));

        // Ajouter les nouveaux équipements
        if (updatedRoom.getEquipments() != null) {
            for (Equipment eq : updatedRoom.getEquipments()) {
                eq.setRoom(existingRoom);
                equipmentRepository.save(eq);
            }
        }

        return ResponseEntity.ok(roomRepository.save(existingRoom));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        Optional<Room> roomOptional = roomRepository.findById(id);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();

            // Supprimer manuellement les équipements liés
            List<Equipment> equipments = equipmentRepository.findByRoomId(id);
            equipmentRepository.deleteAll(equipments);

            // Ensuite supprimer la salle
            roomRepository.delete(room);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
