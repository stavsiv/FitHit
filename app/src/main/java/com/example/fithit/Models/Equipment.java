package com.example.fithit.Models;

import java.util.ArrayList;
import java.util.List;

public class Equipment {
    private int equipmentId;
    private String equipmentName;
    private String description;
    private String imageResource;

    public Equipment(String equipmentName, int equipmentId, String description, String imageResource) {
        this.equipmentName = equipmentName;
        this.equipmentId = equipmentId;
        this.description = description;
        this.imageResource = imageResource;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public static List<Equipment> getAllAvailableEquipment() {
        List<Equipment> equipment = new ArrayList<>();
        return equipment;
    }
}
