package com.example.fithit.Models;

import com.example.fithit.Enums.EquipmentType;


public class Equipment {
    private EquipmentType equipmentType;

    public Equipment(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getImageResource() {
        return equipmentType.getImageFileName();
    }

    public String getDisplayName() {
        return equipmentType.getDisplayName();
    }

    public String toString() {
        return "Equipment{" +
                "type=" + equipmentType.getDisplayName() +
                ", image='" + getImageResource() + '\'' +
                '}';
    }
}
