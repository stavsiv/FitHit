package com.example.fithit.Models;

import com.example.fithit.Enums.EquipmentType;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DatabaseEquipments {
    private static final List<Equipment> equipmentList = new ArrayList<>();

    static {
        for (EquipmentType type : EquipmentType.values()) {
            equipmentList.add(new Equipment(type));
        }
    }
    public static List<Equipment> getAllEquipment() {
        return new ArrayList<>(equipmentList);
    }

    public static List<Equipment> searchEquipmentByName(String searchTerm) {
        return equipmentList.stream()
                .filter(equipment ->
                        equipment.getDisplayName()
                                .toLowerCase()
                                .contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    public static boolean hasRequiredEquipment(List<Equipment> userEquipment, List<Equipment> requiredEquipment) {
        return userEquipment.containsAll(requiredEquipment);
    }
}