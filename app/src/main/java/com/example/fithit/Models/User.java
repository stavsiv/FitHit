package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;

import java.util.List;

public class User {
    private int userId;
    private String userName;
    private int level;
    private DifficultyLevel currentDifficulty;
    private int totalWorkouts;
    private List<Equipment> userEquipment;
    private UserGoals currentGoals;
    private List<WorkoutBank> history;
    private List<Metric> metrics;

    public User(String email, String phone) {
    }


    public DifficultyLevel getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(DifficultyLevel currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public User(int userId, String name, int level, int totalWorkouts, List<Equipment> userEquipment, UserGoals currentGoals, List<WorkoutBank> history, List<Metric> metrics) {
        this.userId = userId;
        this.userName = name;
        this.level = level;
        this.currentDifficulty = DifficultyLevel.BEGINNER;
        this.userEquipment = userEquipment;
        this.currentGoals = currentGoals;
        this.history = history;
        this.metrics = metrics;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public UserGoals getCurrentGoals() {
        return currentGoals;
    }

    public void setCurrentGoals(UserGoals currentGoals) {
        this.currentGoals = currentGoals;
    }

    public List<Equipment> getUserEquipment() {
        return userEquipment;
    }

    public void setUserEquipment(List<Equipment> userEquipment) {
        this.userEquipment = userEquipment;
    }

    public List<WorkoutBank> getHistory() {
        return history;
    }

    public void setHistory(List<WorkoutBank> history) {
        this.history = history;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public void calculateNextLevel() {
        int newLevel = totalWorkouts / 10 + 1;
        if (newLevel > this.level) {
            setLevel(newLevel);
            updateDifficultyLevel();
        }
    }

        public void updateDifficultyLevel() {
            if (this.level >= 1 && this.level <= 4) {
                this.currentDifficulty = DifficultyLevel.BEGINNER;
            } else if (this.level >= 5 && this.level <= 9) {
                this.currentDifficulty = DifficultyLevel.ADVANCED;
            } else if (this.level >= 10) {
                this.currentDifficulty = DifficultyLevel.EXPERT;
            }
        }

    }


