package com.reservation_system.authService.Services;

import com.reservation_system.authService.models.Equipment;
import com.reservation_system.authService.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentRepository equipmentRepository;

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Long id, Equipment equipmentDetails) {
        return equipmentRepository.findById(id).map(equipment -> {
            equipment.setName(equipmentDetails.getName());
            return equipmentRepository.save(equipment);
        }).orElse(null);
    }

    public void deleteEquipment(Long id) {
        equipmentRepository.deleteById(id);
    }
}
