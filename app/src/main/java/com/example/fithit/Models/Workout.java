package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;

import java.util.*;
import java.util.stream.Collectors;

public class Workout {
    private int workoutId;
    private List<Exercise> exercises;     // רשימת תרגילים
    private DifficultyLevel difficulty;   // רמת קושי
    private int requiredLevel;            // רמה נדרשת מהמשתמש
    private int totalDuration;        // זמן משוער בדקות

    private Date date;
    private String type;

    public Date getDate() { return date; }
    public String getType() { return type; }



    public Workout(int id, List<Exercise> exercises, DifficultyLevel difficulty, int requiredLevel, int totalDuration) {
        this.workoutId = id;
        this.exercises = exercises;
        this.difficulty = difficulty;
        this.requiredLevel = requiredLevel;
        this.totalDuration = totalDuration;
    }

    public int getId() {
        return workoutId;
    }

    public void setId(int id) {
        this.workoutId = id;
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

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int estimatedDuration) {
        this.totalDuration = estimatedDuration;
    }

    public void addExercise(Exercise exercise) {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        exercises.add(exercise);
        updateWorkoutDetails();
    }

    public void removeExercise(String exerciseId) {
        exercises.removeIf(exercise -> Objects.equals(exercise.getExerciseId(), exerciseId));
        updateWorkoutDetails();
    }

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

    private void updateWorkoutDetails() {
        // Check for null or empty exercises list
        if (exercises == null || exercises.isEmpty()) {
            this.difficulty = DifficultyLevel.BEGINNER;
            this.totalDuration = 0;
            return;
        }

        // Calculate average difficulty level
        OptionalDouble avgDifficulty = exercises.stream()
                .mapToInt(exercise -> exercise.getDifficultyLevel().getValue())
                .average();

        // Set difficulty based on average
        this.difficulty = avgDifficulty.isPresent() ?
                DifficultyLevel.fromValue((int) avgDifficulty.getAsDouble()) :
                DifficultyLevel.BEGINNER;

        // Calculate total duration
        this.totalDuration = exercises.stream()
                .mapToInt(Exercise::getDurationInMinutes)
                .sum();
    }

        public List<Equipment> getAllRequiredEquipment () {
            return exercises.stream()
                    .flatMap(e -> e.getRequiredEquipment().stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
    }



