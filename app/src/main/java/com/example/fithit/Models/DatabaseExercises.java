package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Enums.ExerciseType;
import com.example.fithit.Enums.MuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseExercises {
    private static final List<Exercise> exercises = new ArrayList<>();
    private static int nextExerciseId = 1;
    private static Map<DifficultyLevel, Integer> getRepetitionsForExercise(String exerciseName) {
        Map<DifficultyLevel, Integer> reps = new HashMap<>();
        switch (exerciseName.toLowerCase()) {
            case "dumbbell squat":
            case "bulgarian split squats":
                reps.put(DifficultyLevel.BEGINNER, 15);
                reps.put(DifficultyLevel.INTERMEDIATE, 30);
                reps.put(DifficultyLevel.EXPERT, 50);
                break;

            case "pull-ups":
                reps.put(DifficultyLevel.BEGINNER, 5);
                reps.put(DifficultyLevel.INTERMEDIATE, 10);
                reps.put(DifficultyLevel.EXPERT, 20);
                break;

            case "push-ups":
                reps.put(DifficultyLevel.BEGINNER, 10);
                reps.put(DifficultyLevel.INTERMEDIATE, 20);
                reps.put(DifficultyLevel.EXPERT, 40);
                break;
            case "barbell bench press":
                reps.put(DifficultyLevel.BEGINNER, 8);
                reps.put(DifficultyLevel.INTERMEDIATE, 12);
                reps.put(DifficultyLevel.EXPERT, 15);
                break;

            case "plank with row":
            case "face pulls":
                reps.put(DifficultyLevel.BEGINNER, 10);
                reps.put(DifficultyLevel.INTERMEDIATE, 15);
                reps.put(DifficultyLevel.EXPERT, 20);
                break;

            case "kettlebell swing":
                reps.put(DifficultyLevel.BEGINNER, 15);
                reps.put(DifficultyLevel.INTERMEDIATE, 25);
                reps.put(DifficultyLevel.EXPERT, 35);
                break;
            // For cardio exercises, reps are in seconds
            case "jumping jacks":
            case "mountain climbers":
                reps.put(DifficultyLevel.BEGINNER, 30);  // 30 seconds
                reps.put(DifficultyLevel.INTERMEDIATE, 45);  // 45 seconds
                reps.put(DifficultyLevel.EXPERT, 60);  // 60 seconds
                break;

            // For stretching exercises, reps are in seconds (hold time)
            case "hamstring stretch":
            case "cobra pose":
            case "wall slides":
                reps.put(DifficultyLevel.BEGINNER, 20);  // 20 seconds hold
                reps.put(DifficultyLevel.INTERMEDIATE, 30);  // 30 seconds hold
                reps.put(DifficultyLevel.EXPERT, 45);  // 45 seconds hold
                break;
            // Default values for other exercises
            default:
                reps.put(DifficultyLevel.BEGINNER, 12);
                reps.put(DifficultyLevel.INTERMEDIATE, 15);
                reps.put(DifficultyLevel.EXPERT, 20);
                break;
        }

        return reps;
    }

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
                MuscleGroup.LEGS,
                getRepetitionsForExercise("Dumbbell Squat")
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
                MuscleGroup.CHEST,
                getRepetitionsForExercise("Push-ups")
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
                MuscleGroup.CHEST,
                getRepetitionsForExercise("Barbell Bench Press")
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
                MuscleGroup.BACK,
                getRepetitionsForExercise("Pull-ups")

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
                MuscleGroup.LEGS,
                getRepetitionsForExercise("Unilateral leg exercise")
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
                MuscleGroup.CORE,
                getRepetitionsForExercise("Plank with Row")

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
                MuscleGroup.BACK,
                getRepetitionsForExercise("Kettlebell Swing")
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
                MuscleGroup.SHOULDERS,
                getRepetitionsForExercise("Face Pulls")
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
                MuscleGroup.LEGS,
                getRepetitionsForExercise("Stretches the back of your thighs")
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
                MuscleGroup.FULL_BODY,
                getRepetitionsForExercise("World's Greatest Stretch")
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
                MuscleGroup.BACK,
                getRepetitionsForExercise("Thoracic Bridge")
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
                MuscleGroup.LEGS,
                getRepetitionsForExercise("90/90 Hip Stretch")
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
                MuscleGroup.SHOULDERS,
                getRepetitionsForExercise("Shoulder and spine mobility")
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
                MuscleGroup.BACK,
                getRepetitionsForExercise("Cobra Pose")
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
                MuscleGroup.SHOULDERS,
                getRepetitionsForExercise("Wall Slides")
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
                MuscleGroup.CORE,
                getRepetitionsForExercise("Single Leg Stand")
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
                MuscleGroup.CORE,
                getRepetitionsForExercise("Bird Dog")
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
                MuscleGroup.LEGS,
                getRepetitionsForExercise("Pistol Squat Progression")
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
                MuscleGroup.CORE,
                getRepetitionsForExercise("Dynamic balance exercise")
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
                MuscleGroup.CORE,
                getRepetitionsForExercise("Stability Ball Pike")
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
                MuscleGroup.LEGS,
                getRepetitionsForExercise("Single Leg Romanian Deadlift")
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
                MuscleGroup.CORE,
                getRepetitionsForExercise("Half-Kneeling Pallof Press")
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
                MuscleGroup.FULL_BODY,
                getRepetitionsForExercise("Jumping Jacks")
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
                MuscleGroup.FULL_BODY,
                getRepetitionsForExercise("Tabata Sprint")
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
                MuscleGroup.FULL_BODY,
                getRepetitionsForExercise("jump Rope Double Unders")
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
                MuscleGroup.LEGS,
                getRepetitionsForExercise("Shuttle Runs")
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
                MuscleGroup.FULL_BODY,
                getRepetitionsForExercise("Mountain Climbers")
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
                MuscleGroup.FULL_BODY,
                getRepetitionsForExercise("Box Jump Burpee")
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
                MuscleGroup.ARMS,
                getRepetitionsForExercise("Battle Rope Waves")
        ));
    }

    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(exercises);
    }

    public static List<Exercise> getExercisesByType(ExerciseType type) {
        return exercises.stream()
                .filter(exercise -> exercise.getExerciseType() == type)
                .collect(Collectors.toList());
    }

    public static List<Exercise> getExercisesByDifficulty(DifficultyLevel difficulty) {
        return exercises.stream()
                .filter(exercise -> exercise.getDifficultyLevel() == difficulty)
                .collect(Collectors.toList());
    }

    public static List<Exercise> getExercisesByTypeAndDifficulty(
            ExerciseType type,
            DifficultyLevel difficulty,
            List<EquipmentType> availableEquipment) {
        return getAllExercises().stream()
                .filter(e -> e.getExerciseType() == type)
                .filter(e -> e.getDifficultyLevel() == difficulty)
                .filter(e -> e.canPerformWithEquipment(availableEquipment))
                .collect(Collectors.toList());
    }
/*    public static List<Exercise> getExercisesByTypeAndEquipment(
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
    }*/
}