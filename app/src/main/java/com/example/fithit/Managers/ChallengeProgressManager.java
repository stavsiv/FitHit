package com.example.fithit.Managers;

import android.util.Log;

import com.example.fithit.Models.Challenge;
import com.example.fithit.Models.ChallengeRecord;
import com.example.fithit.Models.Exercise;
import com.example.fithit.Models.User;
import com.example.fithit.Models.WorkoutRecord;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChallengeProgressManager {
    private static final String TAG = "ChallengeProgress";
    private static ChallengeProgressManager instance;
    private final FirebaseManager firebaseManager;

    private ChallengeProgressManager() {
        firebaseManager = FirebaseManager.getInstance();
    }

    public static ChallengeProgressManager getInstance() {
        if (instance == null) {
            instance = new ChallengeProgressManager();
        }
        return instance;
    }

    public void updateAllChallengesProgress() {
        firebaseManager.getUserChallengeRecords()
                .addOnSuccessListener(records -> {
                    if (records == null || records.isEmpty()) return;

                    firebaseManager.getCurrentUserData()
                            .addOnSuccessListener(user -> {
                                if (user == null) return;

                                boolean recordsUpdated = false;

                                for (ChallengeRecord record : records) {
                                    if (record != null && record.isActive()) {
                                        boolean wasUpdated = updateChallengeProgress(record, user);
                                        if (wasUpdated) {
                                            Task<Void> voidTask = firebaseManager.updateChallengeRecord(record)
                                                    .addOnFailureListener(e -> Log.e(TAG, "Error updating challenge record: " + e.getMessage()));
                                            recordsUpdated = true;
                                        }
                                    }
                                }

                                // If any records were updated, update user data
                                if (recordsUpdated) {
                                    firebaseManager.updateUserData(user)
                                            .addOnFailureListener(e -> Log.e(TAG, "Error updating user data: " + e.getMessage()));
                                }
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error getting user data: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting challenge records: " + e.getMessage()));
    }

    private boolean updateChallengeProgress(ChallengeRecord record, User user) {
        if (record == null || record.getChallenge() == null || !record.isActive()) {
            return false;
        }

        Challenge challenge = record.getChallenge();
        String challengeName = challenge.getName();
        int progress = 0;

        List<WorkoutRecord> workoutHistory = user.getWorkoutHistory();
        if (workoutHistory == null || workoutHistory.isEmpty()) {
            return false;
        }

        long timeWindow;
        switch (challenge.getType()) {
            case "DAILY":
                timeWindow = getStartOfDay();
                break;
            case "WEEKLY":
                timeWindow = getStartOfWeek();
                break;
            case "MONTHLY":
                timeWindow = getStartOfMonth();
                break;
            default:
                timeWindow = 0;
                break;
        }

        switch (challengeName) {
            case "Daily Workout Champion":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() && workout.getDate() >= timeWindow) {
                        progress++;
                    }
                }
                break;

            case "Workout Warrior":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() && workout.getDate() >= timeWindow) {
                        progress++;
                    }
                }
                break;

            case "Strength Builder":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() &&
                            workout.getDate() >= timeWindow &&
                            workout.getWorkout() != null &&
                            workout.getWorkout().getCategory().equalsIgnoreCase("Strength")) {
                        progress++;
                    }
                }
                break;

            case "Cardio Master":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() &&
                            workout.getDate() >= timeWindow &&
                            workout.getWorkout() != null &&
                            workout.getWorkout().getCategory().equalsIgnoreCase("Cardio")) {
                        progress++;
                    }
                }
                break;

            case "Core Power":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() &&
                            workout.getDate() >= timeWindow &&
                            workout.getWorkout() != null &&
                            workout.getWorkout().getCategory().equalsIgnoreCase("Core")) {
                        progress++;
                    }
                }
                break;

            case "Expert Challenger":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() &&
                            workout.getDate() >= timeWindow &&
                            workout.getWorkout() != null &&
                            workout.getWorkout().getDifficultyLevel().toString().equalsIgnoreCase("EXPERT")) {
                        progress++;
                    }
                }
                break;

            case "Fitness Journey":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() && workout.getDate() >= timeWindow) {
                        progress++;
                    }
                }
                break;

            case "Exercise Variety":
                Set<String> muscleGroups = new HashSet<>();
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() &&
                            workout.getDate() >= timeWindow &&
                            workout.getWorkout() != null &&
                            workout.getWorkout().getExercises() != null) {

                        for (Exercise exercise : workout.getWorkout().getExercises()) {
                            muscleGroups.add(exercise.getTargetMuscle().name());
                        }
                    }
                }
                progress = Math.min(muscleGroups.size(), 3);
                break;

            case "Full Body Focus":
                for (WorkoutRecord workout : workoutHistory) {
                    if (workout.isCompleted() &&
                            workout.getDate() >= timeWindow &&
                            workout.getWorkout() != null &&
                            workout.getWorkout().getName().equals("Complete Fitness Journey")) {
                        progress++;
                    }
                }
                break;

            case "Consistency King":
                progress = checkConsistencyChallenge(workoutHistory);
                break;
        }

        int oldProgress = record.getCurrentProgress();
        boolean wasCompleted = record.isCompleted();

        if (progress != oldProgress) {
            boolean newlyCompleted = record.updateProgress(progress);

            if (newlyCompleted && !wasCompleted) {
                user.addHearts(challenge.getHeartsReward());
            }

            return true;
        }

        return false;
    }

    private int checkConsistencyChallenge(List<WorkoutRecord> workoutHistory) {
        long currentTime = System.currentTimeMillis();
        int weeksWithEnoughWorkouts = 0;

        for (int weekOffset = 0; weekOffset < 4; weekOffset++) {
            long weekEndTime = currentTime - (weekOffset * 7 * 24 * 60 * 60 * 1000L);
            long weekStartTime = weekEndTime - (7 * 24 * 60 * 60 * 1000L);

            int weeklyCount = 0;
            for (WorkoutRecord record : workoutHistory) {
                if (record.isCompleted() &&
                        record.getDate() >= weekStartTime &&
                        record.getDate() < weekEndTime) {
                    weeklyCount++;
                }
            }

            if (weeklyCount >= 3) {
                weeksWithEnoughWorkouts++;
            }
        }

        return weeksWithEnoughWorkouts;
    }

    public void addChallengeForUser(Challenge challenge) {
        ChallengeRecord record = new ChallengeRecord(challenge);

        firebaseManager.addChallengeRecord(record)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Challenge added successfully: " + challenge.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding challenge: " + e.getMessage()));
    }



    public void renewChallenge(ChallengeRecord record) {
        if (record == null) return;

        record.renew();
        firebaseManager.updateChallengeRecord(record)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Challenge renewed successfully: " + record.getChallenge()))
                .addOnFailureListener(e -> Log.e(TAG, "Error renewing challenge: " + e.getMessage()));
    }

    private long getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}