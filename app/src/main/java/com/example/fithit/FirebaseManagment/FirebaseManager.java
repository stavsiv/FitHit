package com.example.fithit.FirebaseManagment;

import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.Exercise;
import com.example.fithit.Models.Metric;
import com.example.fithit.Models.User;
import com.example.fithit.Models.Workout;
import com.example.fithit.Enums.DifficultyLevel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final DatabaseReference dbRef;
    private final FirebaseAuth mAuth;

    // Database node names
    private static final String USERS_NODE = "users";
    private static final String EQUIPMENT_NODE = "equipment";
    private static final String EXERCISES_NODE = "exercises";
    private static final String WORKOUTS_NODE = "workouts";
    private static final String METRICS_NODE = "metrics";

    private FirebaseManager() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // Initialize Database Structure
    public void initializeDatabaseStructure() {
        Map<String, Object> dbStructure = new HashMap<>();
        dbStructure.put(USERS_NODE, new HashMap<>());
        dbStructure.put(EQUIPMENT_NODE, new HashMap<>());
        dbStructure.put(EXERCISES_NODE, new HashMap<>());
        dbStructure.put(WORKOUTS_NODE, new HashMap<>());
        dbStructure.put(METRICS_NODE, new HashMap<>());

        dbRef.updateChildren(dbStructure);
    }

    // User Operations
    public Task<Void> createNewUser(String userId, String userName) {
        DatabaseReference userRef = dbRef.child(USERS_NODE).child(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("userName", userName);
        userData.put("level", 1);
        userData.put("currentDifficulty", DifficultyLevel.BEGINNER.toString());
        userData.put("totalWorkouts", 0);
        userData.put("equipment", new HashMap<>());

        return userRef.setValue(userData);
    }

    public void getUserData(String userId, ValueEventListener listener) {
        dbRef.child(USERS_NODE).child(userId).addValueEventListener(listener);
    }

    public Task<Void> updateUserLevel(String userId, int newLevel) {
        return dbRef.child(USERS_NODE).child(userId).child("level").setValue(newLevel);
    }

    // Equipment Operations
    public Task<Void> addEquipment(Equipment equipment) {
        String equipmentId = dbRef.child(EQUIPMENT_NODE).push().getKey();
        return dbRef.child(EQUIPMENT_NODE).child(equipmentId).setValue(equipment);
    }

    public Task<Void> addEquipmentToUser(String userId, String equipmentId) {
        return dbRef.child(USERS_NODE).child(userId)
                .child("equipment").child(equipmentId).setValue(true);
    }

    public void getUserEquipment(String userId, ValueEventListener listener) {
        dbRef.child(USERS_NODE).child(userId).child("equipment").addValueEventListener(listener);
    }

    // Exercise Operations
    public Task<Void> addExercise(Exercise exercise) {
        String exerciseId = dbRef.child(EXERCISES_NODE).push().getKey();
        return dbRef.child(EXERCISES_NODE).child(exerciseId).setValue(exercise);
    }

    public void getExercisesByDifficulty(DifficultyLevel difficulty, ValueEventListener listener) {
        dbRef.child(EXERCISES_NODE)
                .orderByChild("difficultyLevel")
                .equalTo(difficulty.toString())
                .addValueEventListener(listener);
    }

    // Workout Operations
    public Task<Void> addWorkout(Workout workout) {
        String workoutId = dbRef.child(WORKOUTS_NODE).push().getKey();
        return dbRef.child(WORKOUTS_NODE).child(workoutId).setValue(workout);
    }

    public void getWorkoutsByDifficulty(DifficultyLevel difficulty, ValueEventListener listener) {
        dbRef.child(WORKOUTS_NODE)
                .orderByChild("difficulty")
                .equalTo(difficulty.toString())
                .addValueEventListener(listener);
    }

    // Metrics Operations
    public Task<Void> addMetric(String userId, Metric metric) {
        String metricId = dbRef.child(METRICS_NODE).child(userId).push().getKey();
        return dbRef.child(METRICS_NODE).child(userId).child(metricId).setValue(metric);
    }

    public void getUserMetrics(String userId, ValueEventListener listener) {
        dbRef.child(METRICS_NODE).child(userId).addValueEventListener(listener);
    }

    // Example of how to use ValueEventListener
    public void getLatestMetrics(String userId, int limit, final OnMetricsLoadedListener listener) {
        dbRef.child(METRICS_NODE).child(userId)
                .orderByChild("measurementDate")
                .limitToLast(limit)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Metric> metrics = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Metric metric = snapshot.getValue(Metric.class);
                            if (metric != null) {
                                metrics.add(metric);
                            }
                        }
                        listener.onMetricsLoaded(metrics);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }

    // Interface for metrics callback
    public interface OnMetricsLoadedListener {
        void onMetricsLoaded(List<Metric> metrics);
        void onError(String error);
    }

    // Helper method to get current user ID
    public String getCurrentUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        return null;
    }
}