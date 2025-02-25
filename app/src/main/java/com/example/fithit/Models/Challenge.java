package com.example.fithit.Models;

import android.os.Build;
import androidx.annotation.RequiresApi;// לבדוק אם אפשר למחוק
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)// לבדוק אם אפשר למחוק
public class Challenge {
    public static final Challenge DAILY_STEPS_CHAMPION = new Challenge(
            "Daily Steps Champion",
            "Walk 10,000 steps today",
            "DAILY",
            "BEGINNER",
            10,
            10000,
            1
    );

    public static final Challenge DAILY_HYDRATION_HERO = new Challenge(
            "Hydration Hero",
            "Drink 8 glasses of water today",
            "DAILY",
            "BEGINNER",
            5,
            8,
            1
    );

    public static final Challenge WEEKLY_WORKOUT_WARRIOR = new Challenge(
            "Workout Warrior",
            "Complete 5 workouts this week",
            "WEEKLY",
            "INTERMEDIATE",
            25,
            5,
            7
    );

    public static final Challenge WEEKLY_STRENGTH_BUILDER = new Challenge(
            "Strength Builder",
            "Do strength training 3 times this week",
            "WEEKLY",
            "INTERMEDIATE",
            20,
            3,
            7
    );

    // List of all available challenges
    private static final List<Challenge> ALL_CHALLENGES = new ArrayList<>() {{
        add(DAILY_STEPS_CHAMPION);
        add(DAILY_HYDRATION_HERO);
        add(WEEKLY_WORKOUT_WARRIOR);
        add(WEEKLY_STRENGTH_BUILDER);
    }};

    private static int nextChallengeId = 1;

    // Class fields
    private int challengeId;
    private String name;
    private String description;
    private String type;
    private String difficulty;
    private int pointReward;
    private LocalDate startDate;
    private LocalDate endDate;
    private int targetValue;
    private boolean isCompleted;

    // Constructor
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Challenge(String name, String description, String type,
                     String difficulty, int pointReward,
                     int targetValue, int durationDays) {
        this.challengeId = nextChallengeId++;
        this.name = name;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.pointReward = pointReward;
        this.targetValue = targetValue;
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusDays(durationDays);
        this.isCompleted = false;
    }

    // Static methods
    public static Challenge generateRandomChallenge() {
        Random random = new Random();
        return ALL_CHALLENGES.get(random.nextInt(ALL_CHALLENGES.size()));
    }// לבדוק אם אפשר למחוק

    public static List<Challenge> getChallengesByType(String type) {
        List<Challenge> typedChallenges = new ArrayList<>();
        for (Challenge challenge : ALL_CHALLENGES) {
            if (challenge.getType().equals(type)) {
                typedChallenges.add(challenge);
            }
        }
        return typedChallenges;
    }

    // Instance methods
    public boolean checkCompletion(User user, int actualValue) {
        isCompleted = actualValue >= targetValue;
        if (isCompleted) {
            user.addHearts(pointReward);
        }
        return isCompleted;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reset() {
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusDays(type.equals("DAILY") ? 1 : 7);
        this.isCompleted = false;
    }// לבדוק אם אפשר למחוק

    // Getters
    public int getChallengeId() {
        return challengeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getPointReward() {
        return pointReward;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}