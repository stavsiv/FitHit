package com.example.fithit.Models;

import com.example.fithit.Enums.MuscleGroup;

import java.util.List;

public class Exercise {
    private int id;
    private String name;
    private String description;
    private List<Equipment> requiredEquipment;
    private int defaultDuration;
    private int defaultReps;
    private int defaultSets;
    // private String videoUrl;
    private List<String> instructions;
    private MuscleGroup targetMuscle;

    public Exercise(int id, String name, String description, List<Equipment> requiredEquipment, int defaultDuration, int defaultReps, int defaultSets, List<String> instructions, MuscleGroup targetMuscle) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredEquipment = requiredEquipment;
        this.defaultDuration = defaultDuration;
        this.defaultReps = defaultReps;
        this.defaultSets = defaultSets;
        this.instructions = instructions;
        this.targetMuscle = targetMuscle;
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

    public List<Equipment> getRequiredEquipment() {
        return requiredEquipment;
    }

    public void setRequiredEquipment(List<Equipment> requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public int getDefaultReps() {
        return defaultReps;
    }

    public void setDefaultReps(int defaultReps) {
        this.defaultReps = defaultReps;
    }

    public int getDefaultSets() {
        return defaultSets;
    }

    public void setDefaultSets(int defaultSets) {
        this.defaultSets = defaultSets;
    }

    public MuscleGroup getTargetMuscle() {
        return targetMuscle;
    }

    public void setTargetMuscle(MuscleGroup targetMuscle) {
        this.targetMuscle = targetMuscle;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
}
