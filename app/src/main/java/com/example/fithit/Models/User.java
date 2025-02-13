package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String userName;
    private String phone;
    private int age;
    private double weight;
    private boolean wantReminders;
    private int level;
    private DifficultyLevel currentDifficulty;
    private int totalWorkouts;
    private List<Equipment> userEquipment;
    private UserGoals currentGoals;
    private List<WorkoutBank> history;
    private List<Metric> metrics;

    public User() {
    }
    public User(String email, String userName, String phone, int age, double weight, boolean wantReminders) {
        this.email = email;
        this.userName = userName;
        this.phone = phone;
        this.age = age;
        this.weight = weight;
        this.wantReminders = wantReminders;
        this.level = 1;  // Starting level for new users
        this.currentDifficulty = DifficultyLevel.BEGINNER;
        this.totalWorkouts = 0;
        this.userEquipment = new ArrayList<>();
        this.currentGoals = new UserGoals();
        this.history = new ArrayList<>();
        this.metrics = new ArrayList<>();
    }
    public DifficultyLevel getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(DifficultyLevel currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
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

    //get and set
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isWantReminders() {
        return wantReminders;
    }

    public void setWantReminders(boolean wantReminders) {
        this.wantReminders = wantReminders;
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

    public List<Equipment> getUserEquipment() {
        return userEquipment;
    }

    public void setUserEquipment(List<Equipment> userEquipment) {
        this.userEquipment = userEquipment;
    }

    public UserGoals getCurrentGoals() {
        return currentGoals;
    }

    public void setCurrentGoals(UserGoals currentGoals) {
        this.currentGoals = currentGoals;
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
}