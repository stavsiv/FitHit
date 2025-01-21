package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;

import java.util.List;

public class User {
    private int id;
    private String name;
    private int level;
    private DifficultyLevel currentDifficulty;
    private int totalWorkouts;
    private List<Equipment> userEquipment;
    private UserGoals currentGoals;
    private List<WorkoutHistory> history;
    private List<Metric> metrics;


    public DifficultyLevel getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(DifficultyLevel currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public User(int id, String name, int level, int totalWorkouts, List<Equipment> userEquipment, UserGoals currentGoals, List<WorkoutHistory> history, List<Metric> metrics) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.currentDifficulty = DifficultyLevel.BEGINNER;
        this.userEquipment = userEquipment;
        this.currentGoals = currentGoals;
        this.history = history;
        this.metrics = metrics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<WorkoutHistory> getHistory() {
        return history;
    }

    public void setHistory(List<WorkoutHistory> history) {
        this.history = history;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public boolean hasRequiredEquipment(Workout workout) {
        return userEquipment.containsAll(workout.getRequiredEquipment());
    }

    public void calculateNextLevel() {
        int newLevel = totalWorkouts / 10 + 1;
        if (newLevel > this.level) {
            setLevel(newLevel);
            updateDifficultyLevel();
        }
    }

        public void updateDifficultyLevel() {
            if (this.level >= 1 && this.level <= 3) {
                this.currentDifficulty = DifficultyLevel.BEGINNER;
            } else if (this.level >= 4 && this.level <= 6) {
                this.currentDifficulty = DifficultyLevel.INTERMEDIATE;
            } else if (this.level >= 7 && this.level <= 9) {
                this.currentDifficulty = DifficultyLevel.ADVANCED;
            } else if (this.level >= 10) {
                this.currentDifficulty = DifficultyLevel.EXPERT;
            }
        }

    }


