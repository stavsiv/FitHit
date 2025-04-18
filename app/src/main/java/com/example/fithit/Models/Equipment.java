package com.example.fithit.Models;

import androidx.annotation.NonNull;

import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.R;

public class Equipment {
    private EquipmentType equipmentType;
    private boolean isSelected;

    public Equipment(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
        this.isSelected = false;
    }

    public String getDisplayName() {
        return equipmentType.getDisplayName();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        return (R.string.equipment) + '{' +
                (R.string.type)+ "=" + equipmentType.getDisplayName() +
                ',' + R.string.selected + '=' + isSelected +
                '}';
    }

    public int getImageResourceId() {
        return equipmentType.getResourceId();
    }
}