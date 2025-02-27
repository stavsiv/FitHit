package com.example.fithit.Models;

import android.annotation.SuppressLint;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Enums.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Workout implements Serializable {
    private static int nextWorkoutId = 1;
    private int workoutId;
    private String name;
    private String description;
    private List<Exercise> exercises;
    private int estimatedDuration; // in minutes
    private DifficultyLevel difficultyLevel;
    private String category;
    public Workout() {
        exercises = new ArrayList<>();
    }
    public Workout(String name, String description, List<Exercise> exercises, int estimatedDuration, DifficultyLevel difficultyLevel, String category) {
        this.workoutId = nextWorkoutId++;
        this.name = name;
        this.description = description;
        this.exercises = new ArrayList<>(exercises);
        this.estimatedDuration = estimatedDuration;
        this.difficultyLevel = difficultyLevel;
        this.category = category;

        calculateHearts();
    }

    public int calculateHearts() {
        int baseHearts;
        switch (difficultyLevel) {
            case INTERMEDIATE:
                baseHearts = 10;
                break;
            case EXPERT:
                baseHearts = 15;
                break;
            default:
                baseHearts = 5;
                break;
        }

        int durationHearts = (estimatedDuration / 15) * 2;

        int varietyHearts = (int) exercises.stream()
                .map(Exercise::getExerciseType)
                .distinct()
                .count() * 2;

        return baseHearts + durationHearts + varietyHearts;
    }
    //getters and setters
    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
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

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public static Workout generateCustomWorkout(
            int duration,
            int strengthPercentage,
            int cardioPercentage,
            int stretchingPercentage,
            int balancePercentage,
            DifficultyLevel difficultyLevel,
            List<EquipmentType> availableEquipment) {

        // Validate total percentage
        int totalPercentage = strengthPercentage + cardioPercentage + stretchingPercentage + balancePercentage;
        if (totalPercentage != 100) {
            throw new IllegalArgumentException("Total percentage must equal 100");
        }

        // Calculate time for each exercise type (in minutes)
        Map<ExerciseType, Integer> timeDistribution = new HashMap<>();
        timeDistribution.put(ExerciseType.STRENGTH, (duration * strengthPercentage) / 100);
        timeDistribution.put(ExerciseType.CARDIO, (duration * cardioPercentage) / 100);
        timeDistribution.put(ExerciseType.STRETCHING, (duration * stretchingPercentage) / 100);
        timeDistribution.put(ExerciseType.BALANCE, (duration * balancePercentage) / 100);

        List<Exercise> selectedExercises = new ArrayList<>();
        Random random = new Random();

        // Select exercises for each type
        for (Map.Entry<ExerciseType, Integer> entry : timeDistribution.entrySet()) {
            ExerciseType type = entry.getKey();
            int typeMinutes = entry.getValue();

            if (typeMinutes > 0) {
                List<Exercise> availableExercises = DatabaseExercises.getExercisesByTypeAndDifficulty(
                        type,
                        difficultyLevel,
                        availableEquipment
                );

                int remainingTime = typeMinutes;
                while (remainingTime > 0 && !availableExercises.isEmpty()) {
                    int randomIndex = random.nextInt(availableExercises.size());
                    Exercise exercise = availableExercises.get(randomIndex);

                    if (exercise.getDurationInMinutes() <= remainingTime) {
                        selectedExercises.add(exercise);
                        remainingTime -= exercise.getDurationInMinutes();
                    }
                    availableExercises.remove(randomIndex);
                }
            }
        }

        // Sort exercises by type and muscle group
        selectedExercises.sort((e1, e2) -> {
            int typeCompare = e1.getExerciseType().compareTo(e2.getExerciseType());
            if (typeCompare != 0) return typeCompare;
            return e1.getTargetMuscle().compareTo(e2.getTargetMuscle());
        });

        String workoutName = generateWorkoutName(strengthPercentage, cardioPercentage,
                stretchingPercentage, balancePercentage);
        String description = generateWorkoutDescription(duration, strengthPercentage, cardioPercentage,
                stretchingPercentage, balancePercentage,
                difficultyLevel, selectedExercises);

        return new Workout(
                workoutName,
                description,
                selectedExercises,
                duration,
                difficultyLevel,
                "Custom"
        );
    }
    private static String generateWorkoutName(int strength, int cardio,
                                              int stretching, int balance) {
        List<String> focusAreas = new ArrayList<>();
        if (strength >= 40) focusAreas.add("Strength");
        if (cardio >= 40) focusAreas.add("Cardio");
        if (stretching >= 40) focusAreas.add("Flexibility");
        if (balance >= 40) focusAreas.add("Balance");

        if (focusAreas.isEmpty()) {
            return "Balanced Custom Workout";
        } else if (focusAreas.size() == 1) {
            return focusAreas.get(0) + " Focused Workout";
        } else {
            return String.join("-", focusAreas) + " Combo Workout";
        }
    }

    @SuppressLint("DefaultLocale")
    private static String generateWorkoutDescription(int duration, int strength, int cardio,
                                                     int stretching, int balance,
                                                     DifficultyLevel level,
                                                     List<Exercise> exercises) {
        StringBuilder desc = new StringBuilder();
        desc.append(String.format("%d minute %s workout focusing on: ", duration, level.toString().toLowerCase()));

        List<String> components = new ArrayList<>();
        if (strength > 0) components.add(strength + "% strength training");
        if (cardio > 0) components.add(cardio + "% cardio");
        if (stretching > 0) components.add(stretching + "% stretching");
        if (balance > 0) components.add(balance + "% balance");

        desc.append(String.join(", ", components));

        // Add exercise count
        desc.append(String.format("\nTotal exercises: %d", exercises.size()));

        // Add targeted muscle groups
        Set<String> targetedMuscles = exercises.stream()
                .map(exercise -> exercise.getTargetMuscle().name()) // Convert enum to string using .name()
                .collect(Collectors.toSet());

        if (!targetedMuscles.isEmpty()) {
            desc.append("\nTargeted muscle groups: ")
                    .append(String.join(", ", targetedMuscles));
        }

        return desc.toString();
    }
}