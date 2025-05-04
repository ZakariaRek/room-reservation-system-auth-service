package com.reservation_system.authService.controllers;


import com.reservation_system.authService.models.Equipment;
import com.reservation_system.authService.models.Room;
import com.reservation_system.authService.repository.EquipmentRepository;
import com.reservation_system.authService.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipments")

public class EquipmentController {
    @Autowired
    private  EquipmentRepository equipmentRepository;
    @Autowired
    private  RoomRepository roomRepository;

    // 🟢 GET all equipments
    @GetMapping
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    // 🔎 GET equipment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
        return equipmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🆕 POST new equipment (linked to a room)
    @PostMapping("/room/{roomId}")
    public ResponseEntity<Equipment> createEquipment(
            @PathVariable Long roomId,
            @RequestBody Equipment equipment
    ) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isPresent()) {
            equipment.setRoom(roomOpt.get());
            return ResponseEntity.ok(equipmentRepository.save(equipment));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔄 PUT update equipment
    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment updatedEquipment) {
        return equipmentRepository.findById(id).map(equipment -> {
            equipment.setName(updatedEquipment.getName());
            return ResponseEntity.ok(equipmentRepository.save(equipment));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ❌ DELETE equipment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔍 GET all equipments for a given room
    @GetMapping("/room/{roomId}")
    public List<Equipment> getEquipmentsByRoom(@PathVariable Long roomId) {
        return equipmentRepository.findByRoomId(roomId);
    }
}
