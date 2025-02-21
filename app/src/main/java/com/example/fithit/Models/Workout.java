package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Enums.ExerciseType;

import java.util.*;
import java.util.stream.Collectors;

public class Workout {
    private int workoutId;
    private String name;
    private String description;
    private List<Exercise> exercises;
    private int estimatedDuration; // in minutes
    private DifficultyLevel difficultyLevel;
    private String category; // e.g., "Quick", "Full Body", "Cardio Focus" etc.

    private int requiredHearts;

    public Workout(int workoutId, String name, String description, List<Exercise> exercises, int estimatedDuration, DifficultyLevel difficultyLevel, String category) {
        this.workoutId = workoutId;
        this.name = name;
        this.description = description;
        this.exercises = new ArrayList<>(exercises);
        this.estimatedDuration = estimatedDuration;
        this.difficultyLevel = difficultyLevel;
        this.category = category;

        calculateRequiredHearts();
    }

    private void calculateRequiredHearts() {
        int baseHearts;
        switch (difficultyLevel) {
            case BEGINNER:
                baseHearts = 5;
                break;
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

        this.requiredHearts = baseHearts + durationHearts + varietyHearts;
    }

    public int calculateEarnedHearts(int completedExercises) {
        double completionPercentage = (double) completedExercises / exercises.size();

        int baseHearts;
        switch (difficultyLevel) {
            case BEGINNER:
                baseHearts = 3;
                break;
            case INTERMEDIATE:
                baseHearts = 5;
                break;
            case EXPERT:
                baseHearts = 8;
                break;
            default:
                baseHearts = 3;
                break;
        }

        return (int) (baseHearts * completionPercentage);
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



   //לבדוק אם צריך
    public Exercise getExerciseById(String id) {
        return exercises.stream()
                .filter(exercise -> Objects.equals(exercise.getExerciseId(), id))
                .findFirst()
                .orElse(null);
    }

    public List<Exercise> filterByDifficulty(DifficultyLevel difficultyLevel) {
        return exercises.stream()
                .filter(exercise -> exercise.getDifficultyLevel() == difficultyLevel)
                .collect(Collectors.toList());
    }

//    public List<Exercise> filterByMuscleGroup(MuscleGroup muscleGroup) {
//        return exercises.stream()
//                .filter(exercise -> exercise.getMuscleGroup() == muscleGroup)
//                .collect(Collectors.toList());
//    }

//    public Map<MuscleGroup, List<Exercise>> groupExercisesByMuscle() {
//        return exercises.stream()
//                .collect(Collectors.groupingBy(Exercise::getMuscleGroup));
//    }
        public List<Equipment> getAllRequiredEquipment () {
            return exercises.stream()
                    .flatMap(e -> e.getRequiredEquipment().stream())
                    .distinct()
                    .collect(Collectors.toList());
        }

    public static Workout generateCustomWorkout(
            int duration,
            int strengthPercentage,
            int cardioPercentage,
            int stretchingPercentage,
            int balancePercentage,
            DifficultyLevel difficultyLevel,
            List<EquipmentType> availableEquipment) {

        // חישוב זמן לכל סוג תרגיל
        int strengthDuration = (duration * strengthPercentage) / 100;
        int cardioDuration = (duration * cardioPercentage) / 100;
        int stretchingDuration = (duration * stretchingPercentage) / 100;
        int balanceDuration = (duration * balancePercentage) / 100;

        // קבלת תרגילים מתאימים מה-DatabaseExercises
        List<Exercise> selectedExercises = new ArrayList<>();

        // הוספת תרגילי כוח
        if (strengthPercentage > 0) {
            selectedExercises.addAll(
                    DatabaseExercises.getExercisesByTypeAndDifficulty(
                            ExerciseType.STRENGTH,
                            difficultyLevel,
                            availableEquipment
                    )
            );
        }

        // הוספת תרגילי קרדיו
        if (cardioPercentage > 0) {
            selectedExercises.addAll(
                    DatabaseExercises.getExercisesByTypeAndDifficulty(
                            ExerciseType.CARDIO,
                            difficultyLevel,
                            availableEquipment
                    )
            );
        }

        // הוספת תרגילי מתיחות
        if (stretchingPercentage > 0) {
            selectedExercises.addAll(
                    DatabaseExercises.getExercisesByTypeAndDifficulty(
                            ExerciseType.STRETCHING,
                            difficultyLevel,
                            availableEquipment
                    )
            );
        }

        // הוספת תרגילי שיווי משקל
        if (balancePercentage > 0) {
            selectedExercises.addAll(
                    DatabaseExercises.getExercisesByTypeAndDifficulty(
                            ExerciseType.BALANCE,
                            difficultyLevel,
                            availableEquipment
                    )
            );
        }

        // יצירת אימון חדש
        return new Workout(
                generateWorkoutId(), // צריך ליצור מתודה שמייצרת ID ייחודי
                "Custom Workout",
                "Personalized workout based on your preferences",
                selectedExercises,
                duration,
                difficultyLevel,
                "Custom"
        );
    }

    // מתודת עזר ליצירת ID ייחודי
    private static int generateWorkoutId() {
        // כאן צריך להוסיף לוגיקה ליצירת ID ייחודי
        // אפשר להשתמש ב-UUID או במונה פשוט
        return UUID.randomUUID().hashCode();
    }

    }



