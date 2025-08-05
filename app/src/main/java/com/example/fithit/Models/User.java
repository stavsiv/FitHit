package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;

import java.util.ArrayList;
import java.util.List;

public class User {
    private static int nextUserId = 1;
    private static final int HEARTS_FOR_INTERMEDIATE = 100;
    private static final int HEARTS_FOR_EXPERT = 250;
    private String userId;
    private String userName;
    private String phone;
    private int age;
    private boolean wantReminders;
    private DifficultyLevel currentDifficulty;
    private List<Equipment> userEquipment;
    private List<ChallengeRecord> challengeRecords;
    private List<WorkoutRecord> workoutHistory;
    private int totalHearts;

    public User() {
    }

    public User(String userName, String phone, int age, boolean wantReminders) {
        this.userId = String.valueOf(nextUserId++);
        this.userName = userName;
        this.phone = phone;
        this.age = age;
        this.wantReminders = wantReminders;

        this.currentDifficulty = DifficultyLevel.BEGINNER;
        this.userEquipment = new ArrayList<>();
        this.challengeRecords = new ArrayList<>();

        this.workoutHistory = new ArrayList<>();
        this.totalHearts = 0;
    }

    public void addHearts(int heartsEarned) {
        this.totalHearts += heartsEarned;
        checkAndUpdateDifficultyLevel();
    }

    private void checkAndUpdateDifficultyLevel() {
        DifficultyLevel newLevel = calculateDifficultyByHearts();
        if (newLevel.ordinal() > currentDifficulty.ordinal()) {
            this.currentDifficulty = newLevel;
        }
    }

    private DifficultyLevel calculateDifficultyByHearts() {
        if (totalHearts >= HEARTS_FOR_EXPERT) {
            return DifficultyLevel.EXPERT;
        } else if (totalHearts >= HEARTS_FOR_INTERMEDIATE) {
            return DifficultyLevel.INTERMEDIATE;
        }
        return DifficultyLevel.BEGINNER;
    }

    public int getCompletedWorkoutsCount() {
        if (workoutHistory == null) {
            return 0;
        }

        int count = 0;
        for (WorkoutRecord record : workoutHistory) {
            if (record.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalHearts() {
        return totalHearts;
    }

    public String getUserName() {
        return userName;
    }

    public DifficultyLevel getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(DifficultyLevel currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }
    public int getTotalWorkouts() {
        return workoutHistory != null ? workoutHistory.size() : 0;
    }

    public int getLevel() {
        int totalWorkouts = workoutHistory != null ? workoutHistory.size() : 0;
        return (totalWorkouts / 10) + 1;
    }

    public List<WorkoutRecord> getWorkoutHistory() {
        return workoutHistory;
    }

    public void setWorkoutHistory(List<WorkoutRecord> workoutHistory) {
        this.workoutHistory = workoutHistory;
    }
}