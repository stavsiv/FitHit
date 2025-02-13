package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.MuscleGroup;

import java.util.List;

public class Exercise {
    private int exerciseId;
    private String exerciseName;
    private String description;
    private List<Equipment> requiredEquipment;

    private DifficultyLevel difficultyLevel;

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    private int durationInMinutes;

    // private String imageUrl;
    // private String videoUrl;
    private List<String> instructions;
    private MuscleGroup targetMuscle;

    public Exercise(int exerciseId, String exerciseName, String description, List<Equipment> requiredEquipment, int defaultDuration, int defaultReps, int defaultSets, List<String> instructions, MuscleGroup targetMuscle) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.description = description;
        this.requiredEquipment = requiredEquipment;
        this.durationInMinutes = defaultDuration;
        this.instructions = instructions;
        this.targetMuscle = targetMuscle;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
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

    public int getDuration() {
        return durationInMinutes;
    }

    public void setDuration(int duration) {
        this.durationInMinutes = duration;
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

//    public boolean getMuscleGroup() {
//    }
//
//    /*
//     public static Object getMuscleGroup(Object o) {
//    }*/
}
