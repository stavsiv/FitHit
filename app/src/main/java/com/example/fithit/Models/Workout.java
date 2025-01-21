package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.WorkoutType;

import java.util.List;

public class Workout {
    private int id;
    private String name;
    private List<Exercise> exercises;     // רשימת תרגילים
    private DifficultyLevel difficulty;   // רמת קושי
    private int requiredLevel;            // רמה נדרשת מהמשתמש
    private List<Equipment> requiredEquipment; // ציוד נדרש
    private int estimatedDuration;        // זמן משוער בדקות
    private WorkoutType type;             // סוג האימון (כוח, גמישות וכו')

    public Workout(int id, String name, List<Exercise> exercises, DifficultyLevel difficulty, int requiredLevel, List<Equipment> requiredEquipment, int estimatedDuration, WorkoutType type) {
        this.id = id;
        this.name = name;
        this.exercises = exercises;
        this.difficulty = difficulty;
        this.requiredLevel = requiredLevel;
        this.requiredEquipment = requiredEquipment;
        this.estimatedDuration = estimatedDuration;
        this.type = type;
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

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public List<Equipment> getRequiredEquipment() {
        return requiredEquipment;
    }

    public void setRequiredEquipment(List<Equipment> requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public WorkoutType getType() {
        return type;
    }

    public void setType(WorkoutType type) {
        this.type = type;
    }
    public boolean checkUserEquipment(List<Equipment> userEquipment) {
        if (requiredEquipment == null || requiredEquipment.isEmpty()) {
            return true;
        }

        if (userEquipment == null || userEquipment.isEmpty()) {
            return false;
        }

        return userEquipment.containsAll(requiredEquipment);
    }
}

