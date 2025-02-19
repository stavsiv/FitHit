package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userId;
    private String email;
    private String userName;
    private String phone;
    private int age;
    private double weight;// למחוק
    private boolean wantReminders;
    private int level;// למחוק
    private DifficultyLevel currentDifficulty;// דיפולטיבי  // לא מקבלי כפרמטר - לאפס בבנאיי
    private int totalWorkouts; // למחוק
    private List<Equipment> userEquipment; // לא בבנאי מעדכנים באיזור האישי
    private UserGoals currentGoals; // לא בבנאי מעדכנים באיזור האיש
    private List<DatabaseWorkouts> history; //למחוק
    private List<Metric> metrics;// למחוק
// להוסיף מערך של אימונים+מטריקות שמתחיל בגודל אפס וגודל עם כל אימון


    public User() {
    }
    // id לא אמור להתקבל בבנאי אלא משתנה סטטי שעולה באחד עם כל משתנה
    public User(String userId, String email, String username, String phone, Integer age, Double weight, Boolean wantReminders) {
    }

    public User(String userId, String email, String userName, String phone, int age, double weight, boolean wantReminders, int level, DifficultyLevel currentDifficulty, int totalWorkouts, List<Equipment> userEquipment, UserGoals currentGoals, List<DatabaseWorkouts> history, List<Metric> metrics) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.phone = phone;
        this.age = age;
        this.weight = weight;
        this.wantReminders = wantReminders;
        this.level = level;
        this.currentDifficulty = currentDifficulty;
        this.totalWorkouts = totalWorkouts;
        this.userEquipment = userEquipment;
        this.currentGoals = currentGoals;
        this.history = history;
        this.metrics = metrics;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
            this.currentDifficulty = DifficultyLevel.INTERMEDIATE;
        } else if (this.level >= 10) {
            this.currentDifficulty = DifficultyLevel.EXPERT;
        }
    }

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

    public List<DatabaseWorkouts> getHistory() {
        return history;
    }

    public void setHistory(List<DatabaseWorkouts> history) {
        this.history = history;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }


}