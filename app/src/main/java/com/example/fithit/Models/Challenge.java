package com.example.fithit.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Challenge {
    public static final Challenge DAILY_WORKOUT_COMPLETION = new Challenge(
            "Daily Workout Champion",
            "Complete any workout today",
            "DAILY",
            "BEGINNER",
            15,
            1,
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
    public static final Challenge WEEKLY_CARDIO_MASTER = new Challenge(
            "Cardio Master",
            "Complete 3 cardio workouts this week",
            "WEEKLY",
            "INTERMEDIATE",
            20,
            3,
            7
    );

    public static final Challenge WEEKLY_CORE_POWER = new Challenge(
            "Core Power",
            "Complete 2 core-focused workouts this week",
            "WEEKLY",
            "INTERMEDIATE",
            20,
            2,
            7
    );

    public static final Challenge EXPERT_CHALLENGE = new Challenge(
            "Expert Challenger",
            "Complete 2 expert level workouts this week",
            "WEEKLY",
            "EXPERT",
            35,
            2,
            7
    );

    public static final Challenge MONTHLY_FITNESS_JOURNEY = new Challenge(
            "Fitness Journey",
            "Complete 20 workouts this month",
            "MONTHLY",
            "INTERMEDIATE",
            50,
            20,
            30
    );

    public static final Challenge DAILY_EXERCISE_VARIETY = new Challenge(
            "Exercise Variety",
            "Complete exercises targeting 3 different muscle groups today",
            "DAILY",
            "INTERMEDIATE",
            20,
            3,
            1
    );

    public static final Challenge WEEKLY_FULL_BODY = new Challenge(
            "Full Body Focus",
            "Complete the 'Complete Fitness Journey' workout twice this week",
            "WEEKLY",
            "INTERMEDIATE",
            30,
            2,
            7
    );

    public static final Challenge MONTHLY_CONSISTENCY = new Challenge(
            "Consistency King",
            "Work out at least 3 times per week for 4 weeks straight",
            "MONTHLY",
            "EXPERT",
            100,
            12,
            30
    );
    public static final List<Challenge> ALL_CHALLENGES = new ArrayList<>() {{
        add(DAILY_WORKOUT_COMPLETION);
        add(WEEKLY_WORKOUT_WARRIOR);
        add(WEEKLY_STRENGTH_BUILDER);
        add(WEEKLY_CARDIO_MASTER);
        add(WEEKLY_CORE_POWER);
        add(EXPERT_CHALLENGE);
        add(MONTHLY_FITNESS_JOURNEY);
        add(DAILY_EXERCISE_VARIETY);
        add(WEEKLY_FULL_BODY);
        add(MONTHLY_CONSISTENCY);
    }};
    private static int nextChallengeId = 1;

    // Class fields
    private int challengeId;
    private String name;
    private String description;
    private String type;
    private String difficulty;
    private int heartsReward;
    private long startDate;
    private long endDate;
    private int targetValue;
    private boolean isCompleted;
    private int currentProgress;

    public Challenge() {
    }

    public Challenge(String name, String description, String type,
                     String difficulty, int heartReward,
                     int targetValue, int durationDays) {
        this.challengeId = nextChallengeId++;
        this.name = name;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.heartsReward = heartReward;
        this.targetValue = targetValue;
        this.startDate = System.currentTimeMillis();
        this.endDate = this.startDate + (durationDays * 24 * 60 * 60 * 1000L);
        this.isCompleted = false;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    // Getters and Setters
    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getHeartsReward() {
        return heartsReward;
    }

    public void setHeartsReward(int heartReward) {
        this.heartsReward = heartReward;
    }

    public Date getStartDate() {
        return new Date(startDate);
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return new Date(endDate);
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

}