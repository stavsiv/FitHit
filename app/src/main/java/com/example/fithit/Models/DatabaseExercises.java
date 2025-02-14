package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Enums.ExerciseType;
import com.example.fithit.Enums.MuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseExercises {
    private static final List<Exercise> exercises = new ArrayList<>();
    private static int nextExerciseId = 1;

    static {
        // Strength Exercises
        exercises.add(new Exercise(
                nextExerciseId++,
                "Dumbbell Squat",
                ExerciseType.STRENGTH,
                "Basic leg exercise with weight",
                Collections.singletonList(EquipmentType.Dumbbell),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Stand with feet shoulder-width apart",
                        "Hold dumbbells close to your chest",
                        "Lower until thighs are parallel to the ground",
                        "Return to starting position"
                ),
                MuscleGroup.LEGS
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Push-ups",
                ExerciseType.STRENGTH,
                "Classic chest exercise",
                Collections.emptyList(), // No equipment needed
                DifficultyLevel.BEGINNER,
                5,
                Arrays.asList(
                        "Start in a plank position with hands shoulder-width apart",
                        "Lower body until elbows are at 90 degrees",
                        "Push back up to starting position"
                ),
                MuscleGroup.CHEST
        ));
        exercises.add(new Exercise(
                nextExerciseId++,
                "Barbell Bench Press",
                ExerciseType.STRENGTH,
                "Classic chest and triceps exercise",
                Arrays.asList(EquipmentType.Barbell, EquipmentType.Bench),
                DifficultyLevel.INTERMEDIATE,
                8,
                Arrays.asList(
                        "Lie on bench with feet flat on ground",
                        "Grip barbell slightly wider than shoulder width",
                        "Lower bar to chest with control",
                        "Press bar back up to starting position"
                ),
                MuscleGroup.CHEST
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Pull-ups",
                ExerciseType.STRENGTH,
                "Upper body compound exercise",
                Collections.singletonList(EquipmentType.PullUpBar),
                DifficultyLevel.INTERMEDIATE,
                6,
                Arrays.asList(
                        "Hang from pull-up bar with hands wider than shoulders",
                        "Pull yourself up until chin passes bar",
                        "Lower back down with control"
                ),
                MuscleGroup.BACK
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Bulgarian Split Squats",
                ExerciseType.STRENGTH,
                "Unilateral leg exercise",
                Collections.singletonList(EquipmentType.Bench),
                DifficultyLevel.INTERMEDIATE,
                6,
                Arrays.asList(
                        "Place one foot behind you on bench",
                        "Lower back knee toward ground",
                        "Push through front foot to return to start"
                ),
                MuscleGroup.LEGS
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Plank with Row",
                ExerciseType.STRENGTH,
                "Core and back exercise",
                Collections.singletonList(EquipmentType.Dumbbell),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Start in plank position with dumbbells in hands",
                        "Row one dumbbell to hip while maintaining plank",
                        "Lower and repeat on other side"
                ),
                MuscleGroup.CORE
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Kettlebell Swing",
                ExerciseType.STRENGTH,
                "Hip hinge power exercise",
                Collections.singletonList(EquipmentType.Kettlebell),
                DifficultyLevel.INTERMEDIATE,
                6,
                Arrays.asList(
                        "Stand with feet shoulder-width apart",
                        "Hinge at hips and swing kettlebell between legs",
                        "Thrust hips forward to swing kettlebell to shoulder height"
                ),
                MuscleGroup.BACK
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Face Pulls",
                ExerciseType.STRENGTH,
                "Rear deltoid and upper back exercise",
                Collections.singletonList(EquipmentType.ResistanceBand),
                DifficultyLevel.BEGINNER,
                4,
                Arrays.asList(
                        "Attach band at head height",
                        "Pull band towards face, spreading hands apart",
                        "Squeeze shoulder blades together"
                ),
                MuscleGroup.SHOULDERS
        ));
        // Stretching Exercises
        exercises.add(new Exercise(
                nextExerciseId++,
                "Hamstring Stretch",
                ExerciseType.STRETCHING,
                "Stretches the back of your thighs",
                Collections.emptyList(),
                DifficultyLevel.BEGINNER,
                3,
                Arrays.asList(
                        "Sit on the floor with legs straight",
                        "Reach for your toes",
                        "Hold for 30 seconds"
                ),
                MuscleGroup.LEGS
        ));
        exercises.add(new Exercise(
                nextExerciseId++,
                "World's Greatest Stretch",
                ExerciseType.STRETCHING,
                "Full body mobility exercise",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Start in lunge position",
                        "Place hand inside front foot and rotate torso",
                        "Reach arm to sky and hold"
                ),
                MuscleGroup.FULL_BODY
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Thoracic Bridge",
                ExerciseType.STRETCHING,
                "Upper back mobility",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                4,
                Arrays.asList(
                        "Start on hands and knees",
                        "Place one hand behind head",
                        "Rotate torso and follow elbow with eyes"
                ),
                MuscleGroup.BACK
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "90/90 Hip Stretch",
                ExerciseType.STRETCHING,
                "Deep hip mobility exercise",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Sit with both legs bent at 90 degrees",
                        "Keep back straight",
                        "Lean forward to feel stretch"
                ),
                MuscleGroup.LEGS
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Thread the Needle",
                ExerciseType.STRETCHING,
                "Shoulder and spine mobility",
                Collections.emptyList(),
                DifficultyLevel.BEGINNER,
                3,
                Arrays.asList(
                        "Start on hands and knees",
                        "Slide one arm under body, reaching across",
                        "Return to start and repeat other side"
                ),
                MuscleGroup.SHOULDERS
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Cobra Pose",
                ExerciseType.STRETCHING,
                "Spine extension stretch",
                Collections.emptyList(),
                DifficultyLevel.BEGINNER,
                3,
                Arrays.asList(
                        "Lie face down",
                        "Press chest up keeping hips on ground",
                        "Hold position and breathe"
                ),
                MuscleGroup.BACK
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Wall Slides",
                ExerciseType.STRETCHING,
                "Shoulder mobility exercise",
                Collections.emptyList(),
                DifficultyLevel.BEGINNER,
                4,
                Arrays.asList(
                        "Stand with back against wall",
                        "Slide arms up wall keeping elbows bent",
                        "Return to start position"
                ),
                MuscleGroup.SHOULDERS
        ));

        // Balance Exercises
        exercises.add(new Exercise(
                nextExerciseId++,
                "Single Leg Stand",
                ExerciseType.BALANCE,
                "Basic balance exercise",
                Collections.emptyList(),
                DifficultyLevel.BEGINNER,
                2,
                Arrays.asList(
                        "Stand on one leg",
                        "Maintain balance for 30 seconds",
                        "Switch legs and repeat"
                ),
                MuscleGroup.CORE
        ));
        exercises.add(new Exercise(
                nextExerciseId++,
                "Bird Dog",
                ExerciseType.BALANCE,
                "Core stability exercise",
                Collections.emptyList(),
                DifficultyLevel.BEGINNER,
                4,
                Arrays.asList(
                        "Start on hands and knees",
                        "Extend opposite arm and leg",
                        "Hold position maintaining balance"
                ),
                MuscleGroup.CORE
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Pistol Squat Progression",
                ExerciseType.BALANCE,
                "Advanced unilateral leg exercise",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                6,
                Arrays.asList(
                        "Stand on one leg",
                        "Lower into single leg squat",
                        "Return to standing using one leg"
                ),
                MuscleGroup.LEGS
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Bosu Ball Mountain Climbers",
                ExerciseType.BALANCE,
                "Dynamic balance exercise",
                Collections.singletonList(EquipmentType.BosuBall),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Place hands on Bosu ball in plank position",
                        "Alternate bringing knees to chest",
                        "Maintain stability throughout"
                ),
                MuscleGroup.CORE
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Stability Ball Pike",
                ExerciseType.BALANCE,
                "Advanced core stability",
                Collections.singletonList(EquipmentType.StabilityBall),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Start in plank with feet on ball",
                        "Pike hips up bringing ball closer to hands",
                        "Return to plank with control"
                ),
                MuscleGroup.CORE
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Single Leg Romanian Deadlift",
                ExerciseType.BALANCE,
                "Unilateral balance and strength",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Stand on one leg",
                        "Hinge at hips while lifting other leg behind",
                        "Return to standing while maintaining balance"
                ),
                MuscleGroup.LEGS
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Half-Kneeling Pallof Press",
                ExerciseType.BALANCE,
                "Anti-rotation core stability",
                Collections.singletonList(EquipmentType.ResistanceBand),
                DifficultyLevel.INTERMEDIATE,
                4,
                Arrays.asList(
                        "Kneel with one knee up",
                        "Press band straight out",
                        "Resist rotation while holding position"
                ),
                MuscleGroup.CORE
        ));

        // Cardio Exercises
        exercises.add(new Exercise(
                nextExerciseId++,
                "Jumping Jacks",
                ExerciseType.CARDIO,
                "High-intensity aerobic exercise",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Start standing with feet together and arms at sides",
                        "Jump while spreading legs and raising arms above head",
                        "Jump back to starting position"
                ),
                MuscleGroup.FULL_BODY
        ));
        exercises.add(new Exercise(
                nextExerciseId++,
                "Tabata Sprint",
                ExerciseType.CARDIO,
                "High-intensity interval training",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                8,
                Arrays.asList(
                        "Sprint at maximum effort for 20 seconds",
                        "Rest for 10 seconds",
                        "Repeat 8 times"
                ),
                MuscleGroup.FULL_BODY
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Jump Rope Double Unders",
                ExerciseType.CARDIO,
                "Advanced jump rope technique",
                Collections.singletonList(EquipmentType.JumpRope),
                DifficultyLevel.INTERMEDIATE,
                6,
                Arrays.asList(
                        "Jump higher than normal jump rope",
                        "Swing rope twice under feet in one jump",
                        "Land softly and repeat"
                ),
                MuscleGroup.FULL_BODY
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Shuttle Runs",
                ExerciseType.CARDIO,
                "Speed and agility drill",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                7,
                Arrays.asList(
                        "Place markers 25 feet apart",
                        "Sprint between markers",
                        "Touch ground at each turn"
                ),
                MuscleGroup.LEGS
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Mountain Climbers",
                ExerciseType.CARDIO,
                "Full body cardio exercise",
                Collections.emptyList(),
                DifficultyLevel.INTERMEDIATE,
                5,
                Arrays.asList(
                        "Start in plank position",
                        "Alternate bringing knees to chest quickly",
                        "Keep hips stable throughout"
                ),
                MuscleGroup.FULL_BODY
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Box Jump Burpee",
                ExerciseType.CARDIO,
                "Explosive full body exercise",
                Collections.singletonList(EquipmentType.PlyoBox),
                DifficultyLevel.INTERMEDIATE,
                8,
                Arrays.asList(
                        "Perform burpee in front of box",
                        "Jump onto box from burpee",
                        "Step down and repeat"
                ),
                MuscleGroup.FULL_BODY
        ));

        exercises.add(new Exercise(
                nextExerciseId++,
                "Battle Rope Waves",
                ExerciseType.CARDIO,
                "Upper body cardio",
                Collections.singletonList(EquipmentType.BattleRopes),
                DifficultyLevel.INTERMEDIATE,
                6,
                Arrays.asList(
                        "Hold one rope in each hand",
                        "Create alternating waves with arms",
                        "Maintain constant movement"
                ),
                MuscleGroup.ARMS
        ));
    }

    // Get all exercises
    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(exercises);
    }

    // Get exercises by type
    public static List<Exercise> getExercisesByType(ExerciseType type) {
        return exercises.stream()
                .filter(exercise -> exercise.getExerciseType() == type)
                .collect(Collectors.toList());
    }

    // Get exercises by difficulty level
    public static List<Exercise> getExercisesByDifficulty(DifficultyLevel difficulty) {
        return exercises.stream()
                .filter(exercise -> exercise.getDifficultyLevel() == difficulty)
                .collect(Collectors.toList());
    }

    // Get exercises by target muscle
    public static List<Exercise> getExercisesByMuscleGroup(MuscleGroup muscleGroup) {
        return exercises.stream()
                .filter(exercise -> exercise.getTargetMuscle() == muscleGroup)
                .collect(Collectors.toList());
    }

    public static List<Exercise> getExercisesByEquipmentAndDifficulty(
            List<EquipmentType> availableEquipment,
            DifficultyLevel difficultyLevel) {
        return exercises.stream()
                .filter(exercise -> exercise.getDifficultyLevel() == difficultyLevel)
                .filter(exercise -> exercise.canPerformWithEquipment(availableEquipment))
                .collect(Collectors.toList());
    }
    public static List<Exercise> getExercisesByTypeAndDifficulty(
            ExerciseType type,
            DifficultyLevel difficulty,
            List<EquipmentType> availableEquipment) {
        return exercises.stream()
                .filter(exercise -> exercise.getExerciseType() == type)
                .filter(exercise -> exercise.getDifficultyLevel() == difficulty)
                .filter(exercise -> exercise.canPerformWithEquipment(availableEquipment))
                .collect(Collectors.toList());
    }
    public static List<Exercise> getExercisesByTypeAndEquipment(
            ExerciseType type,
            List<EquipmentType> availableEquipment) {
        return exercises.stream()
                .filter(exercise -> exercise.getExerciseType() == type)
                .filter(exercise -> exercise.canPerformWithEquipment(availableEquipment))
                .collect(Collectors.toList());
    }
    public static Exercise getExerciseById(int id) {
        return exercises.stream()
                .filter(exercise -> exercise.getExerciseId() == id)
                .findFirst()
                .orElse(null);
    }
}