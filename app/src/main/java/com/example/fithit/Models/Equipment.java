package com.example.fithit.Models;

import java.util.ArrayList;
import java.util.List;

public class Equipment {
    private int id;
    private String name;
    private String description;
    private String imageResource;

    public Equipment(String name, int id, String description, String imageResource) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
