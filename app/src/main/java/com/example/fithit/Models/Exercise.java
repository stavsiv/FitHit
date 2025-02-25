package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Enums.ExerciseType;
import com.example.fithit.Enums.MuscleGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exercise {
    private int exerciseId;
    private String exerciseName;
    private ExerciseType exerciseType;
    private String description;
    private final List<EquipmentType> requiredEquipmentTypes;
    private DifficultyLevel difficultyLevel;
    private int durationInMinutes;
    private List<String> instructions;
    private MuscleGroup targetMuscle;
    private final Map<DifficultyLevel, Integer> repetitionsByDifficulty;

    public Exercise() {
        requiredEquipmentTypes = new ArrayList<>();
        instructions = new ArrayList<>();
        repetitionsByDifficulty = new HashMap<>();
    }
    public Exercise(int exerciseId,
                    String exerciseName,
                    ExerciseType exerciseType,
                    String description,
                    List<EquipmentType> requiredEquipmentTypes,
                    DifficultyLevel difficultyLevel,
                    int durationInMinutes,
                    List<String> instructions,
                    MuscleGroup targetMuscle,
                    Map<DifficultyLevel, Integer> repetitionsByDifficulty) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.exerciseType = exerciseType;
        this.description = description;
        this.requiredEquipmentTypes = requiredEquipmentTypes;
        this.difficultyLevel = difficultyLevel;
        this.durationInMinutes = durationInMinutes;
        this.instructions = instructions;
        this.targetMuscle = targetMuscle;
        this.repetitionsByDifficulty = repetitionsByDifficulty;
    }

    // Getters
    public int getExerciseId() {
        return exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public String getDescription() {
        return description;
    }

    public List<EquipmentType> getRequiredEquipmentTypes() {
        return new ArrayList<>(requiredEquipmentTypes);
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public List<String> getInstructions() {
        return new ArrayList<>(instructions);
    }

    public MuscleGroup getTargetMuscle() {
        return targetMuscle;
    }
    public int getRepetitionsForDifficulty(DifficultyLevel difficulty) {
        return repetitionsByDifficulty.getOrDefault(difficulty, 0);
    }

    public boolean canPerformWithEquipment(List<EquipmentType> availableEquipment) {
        if (this.requiredEquipmentTypes.isEmpty()) {
            return true;
        }
        return availableEquipment.containsAll(this.requiredEquipmentTypes);
    }
    @Override
    public String toString() {
        return "Exercise{" +
                "name='" + exerciseName + '\'' +
                ", type=" + exerciseType.name() +
                ", difficulty=" + difficultyLevel +
                ", duration=" + durationInMinutes + " minutes" +
                ", reps=" + getRepetitionsForDifficulty(difficultyLevel) +
                '}';
    }
}