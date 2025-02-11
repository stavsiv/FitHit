package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.WorkoutType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Workout {
    private int workoutId;
    private WorkoutType type;             // סוג האימון (כוח, גמישות וכו')
    private List<Exercise> exercises;     // רשימת תרגילים
    private DifficultyLevel difficulty;   // רמת קושי
    private int requiredLevel;            // רמה נדרשת מהמשתמש
    private int estimatedDuration;        // זמן משוער בדקות

    public Workout(int id, String name, List<Exercise> exercises, DifficultyLevel difficulty, int requiredLevel, List<Equipment> requiredEquipment, int estimatedDuration, WorkoutType type) {
        this.workoutId = id;
        this.exercises = exercises;
        this.difficulty = difficulty;
        this.requiredLevel = requiredLevel;
        this.estimatedDuration = estimatedDuration;
        this.type = type;
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

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
        updateWorkoutDetails();
    }

    public void removeExercise(String exerciseId) {
        exercises.removeIf(exercise -> exercise.getExerciseId());
        updateWorkoutDetails();
    }

    public Exercise getExerciseById(String id) {
        return exercises.stream()
                .filter(exercise -> exercise.getExerciseId())
                .findFirst()
                .orElse(null);
    }

    public List<Exercise> filterByDifficulty(DifficultyLevel difficultyLevel) {
        return exercises.stream()
                .filter(exercise -> exercise.getDifficultyLevel() == difficultyLevel)
                .collect(Collectors.toList());
    }

    public List<Exercise> filterByEquipment(Equipment equipment) {
        return exercises.stream()
                .filter(exercise -> exercise.getRequiredEquipment().contains(equipment))
                .collect(Collectors.toList());
    }

    public List<Exercise> filterByMuscleGroup(MuscleGroup muscleGroup) {
        return exercises.stream()
                .filter(exercise -> exercise.getMuscleGroup() == muscleGroup)
                .collect(Collectors.toList());
    }

    private void updateWorkoutDetails() {
        OptionalDouble avgDifficulty = exercises.stream()
                .mapToInt(e -> e.getDifficultyLevel().getValue())
                .average();

        this.workoutLevel = avgDifficulty.isPresent() ?
                DifficultyLevel.fromValue((int)avgDifficulty.getAsDouble()) :
                DifficultyLevel.BEGINNER;

        this.totalDuration = exercises.stream()
                .mapToInt(Exercise::getDurationInMinutes)
                .sum();
    }

    public List<Equipment> getAllRequiredEquipment() {
        return exercises.stream()
                .flatMap(e -> e.getRequiredEquipment().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<MuscleGroup, List<Exercise>> groupExercisesByMuscle() {
        return exercises.stream()
                .collect(Collectors.groupingBy(Exercise::getMuscleGroup));
    }

    public Collection<?> getRequiredEquipment() {
    }
}

