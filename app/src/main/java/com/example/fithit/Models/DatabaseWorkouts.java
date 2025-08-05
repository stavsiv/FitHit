package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.ExerciseType;
import com.example.fithit.Enums.MuscleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseWorkouts {
    private static final List<Workout> workouts = new ArrayList<>();

    static {
        List<Exercise> quickStretchExercises = new ArrayList<>();
        quickStretchExercises.add(DatabaseExercises.getExercisesByType(ExerciseType.STRETCHING)
                .stream()
                .filter(e -> e.getExerciseName().equals("Hamstring Stretch"))
                .findFirst()
                .get());
        quickStretchExercises.add(DatabaseExercises.getExercisesByType(ExerciseType.BALANCE)
                .stream()
                .filter(e -> e.getExerciseName().equals("Single Leg Stand"))
                .findFirst()
                .get());

        workouts.add(new Workout(
                "Quick Stretch & Balance",
                "A quick 15-minute routine combining basic stretches and balance exercises. Perfect for morning or pre-workout.",
                quickStretchExercises,
                15,
                DifficultyLevel.BEGINNER,
                "Quick"
        ));

        // Cardio Blast (30 minutes)
        List<Exercise> cardioExercises = DatabaseExercises.getExercisesByType(ExerciseType.CARDIO)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        workouts.add(new Workout(
                "Cardio Blast",
                "High-intensity cardio workout to get your heart pumping. Great for burning calories and improving endurance.",
                cardioExercises,
                30,
                DifficultyLevel.INTERMEDIATE,
                "Cardio"
        ));

        // Full Body Strength (45 minutes)
        List<Exercise> strengthExercises = DatabaseExercises.getExercisesByType(ExerciseType.STRENGTH)
                .stream()
                .limit(8)
                .collect(Collectors.toList());

        workouts.add(new Workout(
                "Full Body Strength",
                "Comprehensive strength training targeting all major muscle groups.",
                strengthExercises,
                45,
                DifficultyLevel.INTERMEDIATE,
                "Strength"
        ));

        // All-In-One (60 minutes)
        List<Exercise> allInExercises = new ArrayList<>();
        // Add 2 exercises from each type
        allInExercises.addAll(DatabaseExercises.getExercisesByType(ExerciseType.CARDIO)
                .stream().limit(2).collect(Collectors.toList()));
        allInExercises.addAll(DatabaseExercises.getExercisesByType(ExerciseType.STRENGTH)
                .stream().limit(2).collect(Collectors.toList()));
        allInExercises.addAll(DatabaseExercises.getExercisesByType(ExerciseType.BALANCE)
                .stream().limit(2).collect(Collectors.toList()));
        allInExercises.addAll(DatabaseExercises.getExercisesByType(ExerciseType.STRETCHING)
                .stream().limit(2).collect(Collectors.toList()));

        workouts.add(new Workout(
                "Complete Fitness Journey",
                "The ultimate workout combining cardio, strength, balance, and flexibility training.",
                allInExercises,
                60,
                DifficultyLevel.INTERMEDIATE,
                "Full Body"
        ));

        // Core Focus (20 minutes)
        List<Exercise> coreExercises = DatabaseExercises.getAllExercises()
                .stream()
                .filter(e -> e.getTargetMuscle() == MuscleGroup.CORE)
                .limit(6)
                .collect(Collectors.toList());

        workouts.add(new Workout(
                "Core Power",
                "Focused core workout to build strength and stability in your midsection.",
                coreExercises,
                20,
                DifficultyLevel.INTERMEDIATE,
                "Core"
        ));
    }

    // Get all workouts
    public static List<Workout> getAllWorkouts() {
        return new ArrayList<>(workouts);
    }

    // Get workout by ID
    public static Workout getWorkoutById(int id) {
        return workouts.stream()
                .filter(workout -> workout.getWorkoutId() == id)
                .findFirst()
                .orElse(null);
    }
}