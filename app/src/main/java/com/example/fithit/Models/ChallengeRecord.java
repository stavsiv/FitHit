package com.example.fithit.Models;

import java.util.Date;

public class ChallengeRecord {
    private Challenge challenge;
    private long dateAdded;
    private long startDate;
    private long endDate;
    private int currentProgress;
    private boolean isCompleted;

    public ChallengeRecord() {
    }

    public ChallengeRecord(Challenge challenge) {
        this.challenge = challenge;
        this.dateAdded = System.currentTimeMillis();
        this.startDate = System.currentTimeMillis();

        int durationDays;
        if ("DAILY".equals(challenge.getType())) {
            durationDays = 1;
        } else if ("WEEKLY".equals(challenge.getType())) {
            durationDays = 7;
        } else {
            durationDays = 30;
        }

        this.endDate = this.startDate + (durationDays * 24 * 60 * 60 * 1000L);
        this.currentProgress = 0;
        this.isCompleted = false;
    }

    public boolean updateProgress(int newProgress) {
        this.currentProgress = newProgress;

        if (newProgress >= getTargetValue() && !isCompleted) {
            this.isCompleted = true;
            return true;
        }

        return false;
    }
    public boolean incrementProgress(User user, int amount) {
        this.currentProgress += amount;

        if (currentProgress >= getTargetValue() && !isCompleted) {
            this.isCompleted = true;
            if (challenge != null) {
                user.addHearts(challenge.getHeartsReward());
            }
            return true;
        }

        return false;
    }

    public int getProgressPercentage() {
        if (challenge != null && challenge.getTargetValue() > 0) {
            return (currentProgress * 100) / challenge.getTargetValue();
        }
        return 0;
    }

    public int getRemainingValue() {
        if (challenge != null) {
            return Math.max(0, challenge.getTargetValue() - currentProgress);
        }
        return 0;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endDate;
    }

    public int getDaysRemaining() {
        long diff = endDate - System.currentTimeMillis();
        if (diff <= 0) return 0;
        return (int) (diff / (24 * 60 * 60 * 1000L));
    }

    public boolean isActive() {
        return !isCompleted && !isExpired();
    }

    public void renew() {
        this.startDate = System.currentTimeMillis();
        int durationDays;
        if ("DAILY".equals(getChallengeType())) {
            durationDays = 1;
        } else if ("WEEKLY".equals(getChallengeType())) {
            durationDays = 7;
        } else {
            // MONTHLY
            durationDays = 30;
        }
        this.endDate = this.startDate + (durationDays * 24 * 60 * 60 * 1000L);
        this.currentProgress = 0;
        this.isCompleted = false;
    }

    public boolean trackWorkoutCompletion(WorkoutRecord workoutRecord) {
        if (challenge == null || isCompleted || !workoutRecord.isCompleted()) {
            return false;
        }

        boolean wasUpdated = false;
        Workout workout = workoutRecord.getWorkout();
        if (workout == null) {
            return false;
        }

        String challengeName = challenge.getName();

        switch (challengeName) {
            case "Daily Workout Champion":
            case "Workout Warrior":
            case "Fitness Journey":
                currentProgress++;
                wasUpdated = true;
                break;

            case "Strength Builder":
                if (workout.getCategory().equalsIgnoreCase("Strength")) {
                    currentProgress++;
                    wasUpdated = true;
                }
                break;

            case "Cardio Master":
                if (workout.getCategory().equalsIgnoreCase("Cardio")) {
                    currentProgress++;
                    wasUpdated = true;
                }
                break;

            case "Core Power":
                if (workout.getCategory().equalsIgnoreCase("Core")) {
                    currentProgress++;
                    wasUpdated = true;
                }
                break;

            case "Expert Challenger":
                if (workout.getDifficultyLevel().toString().equals("EXPERT")) {
                    currentProgress++;
                    wasUpdated = true;
                }
                break;

            case "Full Body Focus":
                if (workout.getName().equals("Complete Fitness Journey")) {
                    currentProgress++;
                    wasUpdated = true;
                }
                break;

            case "Exercise Variety":
                if (workout.getExercises() != null) {
                    int uniqueMuscleGroups = (int) workout.getExercises().stream()
                            .map(Exercise::getTargetMuscle)
                            .distinct()
                            .count();

                    currentProgress = Math.max(currentProgress, uniqueMuscleGroups);
                    wasUpdated = true;
                }
                break;
        }

        // Check if challenge is completed
        if (currentProgress >= challenge.getTargetValue() && !isCompleted) {
            isCompleted = true;
        }

        return wasUpdated;
    }    public String getChallengeName() {
        return challenge != null ? challenge.getName() : "";
    }

    public String getChallengeDescription() {
        return challenge != null ? challenge.getDescription() : "";
    }

    public int getPointReward() {
        return challenge != null ? challenge.getHeartsReward() : 0;
    }

    public String getChallengeType() {
        return challenge != null ? challenge.getType() : "";
    }

    public int getTargetValue() {
        return challenge != null ? challenge.getTargetValue() : 0;
    }

    // ---------- Getters and Setters ----------

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public Date getDateAddedAsDate() {
        return new Date(dateAdded);
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getStartDate() {
        return startDate;
    }

    public Date getStartDateAsDate() {
        return new Date(startDate);
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public Date getEndDateAsDate() {
        return new Date(endDate);
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}