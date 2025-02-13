package com.example.fithit.FirebaseManagment;

import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.Exercise;
import com.example.fithit.Models.Metric;
import com.example.fithit.Models.User;
import com.example.fithit.Models.Workout;
import com.example.fithit.Enums.DifficultyLevel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

//    public void initializeDatabaseStructure() {
//        Map<String, Object> dbStructure = new HashMap<>();
//        dbStructure.put(USERS_NODE, new HashMap<>());
//        dbStructure.put(EQUIPMENT_NODE, new HashMap<>());
//        dbStructure.put(EXERCISES_NODE, new HashMap<>());
//        dbStructure.put(WORKOUTS_NODE, new HashMap<>());
//        dbStructure.put(METRICS_NODE, new HashMap<>());
//
//        dbRef.updateChildren(dbStructure);
//    }

    public Task<Void> createNewUser(String userId, String email, String username,
                                    String phone, int age, double weight, boolean wantReminders) {
        if (userId == null) {
            return Tasks.forException(new Exception("User ID cannot be null"));
        }
        DatabaseReference userRef = dbRef.child(USERS_NODE).child(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("userName", username);
        userData.put("phone", phone);
        userData.put("age", age);
        userData.put("weight", weight);
        userData.put("wantReminders", wantReminders);

        // Initialize default values
        userData.put("level", 1);
        userData.put("currentDifficulty", DifficultyLevel.BEGINNER.toString());
        userData.put("totalWorkouts", 0);
        userData.put("equipment", new HashMap<>());
        userData.put("metrics", new HashMap<>());
        userData.put("history", new ArrayList<>());

        return userRef.setValue(userData);
    }
    public Task<User> getCurrentUserData() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        return dbRef.child(USERS_NODE).child(userId).get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    DataSnapshot dataSnapshot = task.getResult();
                    User user = createUserFromSnapshot(dataSnapshot);
                    if (user == null) {
                        throw new Exception("Failed to parse user data");
                    }
                    // הוספת בדיקות נוספות
                    if (user.getLevel() <= 0 || user.getCurrentDifficulty() == null) {
                        throw new Exception("Invalid user data state");
                    }
                    return user;
                });
    }

    public User createUserFromSnapshot(DataSnapshot snapshot) {
        try {
            String email = snapshot.child("email").getValue(String.class);
            String username = snapshot.child("userName").getValue(String.class);
            String phone = snapshot.child("phone").getValue(String.class);
            Integer age = snapshot.child("age").getValue(Integer.class);
            Double weight = snapshot.child("weight").getValue(Double.class);
            Boolean wantReminders = snapshot.child("wantReminders").getValue(Boolean.class);

            if (email == null || username == null || phone == null ||
                    age == null || weight == null || wantReminders == null) {
                return null;
            }

            return new User(email, username, phone, age, weight, wantReminders);
        } catch (Exception e) {
            return null;
        }
    }

//    public Task<User> getUserProfile(String userId) {
//        if (userId == null) {
//            return Tasks.forException(new Exception("User ID cannot be null"));
//        }
//
//        return dbRef.child(USERS_NODE).child(userId).get()
//                .continueWith(task -> {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    User user = createUserFromSnapshot(task.getResult());
//                    if (user == null) {
//                        throw new Exception("Failed to parse user data");
//                    }
//                    return user;
//                });
//    }

    public Task<Void> updateUserProfile(String userId, String username, String phone,
                                        int age, double weight, boolean wantReminders) {
        if (userId == null) {
            return Tasks.forException(new Exception("User ID cannot be null"));
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("/userName", username);
        updates.put("/phone", phone);
        updates.put("/age", age);
        updates.put("/weight", weight);
        updates.put("/wantReminders", wantReminders);

        return dbRef.child(USERS_NODE).child(userId).updateChildren(updates);
    }
    public String getCurrentUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        return null;
    }



    public Task<Void> addEquipment(Equipment equipment) {
        String equipmentId = dbRef.child(EQUIPMENT_NODE).push().getKey();
        if (equipmentId == null) {
            return Tasks.forException(new Exception("Failed to generate equipment ID"));
        }
        return dbRef.child(EQUIPMENT_NODE).child(equipmentId).setValue(equipment);
    }

    public Task<Void> addEquipmentToUser(String userId, String equipmentId) {
        if (userId == null || equipmentId == null) {
            return Tasks.forException(new Exception("User ID and Equipment ID cannot be null"));
        }
        return dbRef.child(USERS_NODE).child(userId)
                .child("equipment").child(equipmentId).setValue(true);
    }

    public Task<List<Equipment>> getUserEquipment(String userId) {
        if (userId == null) {
            return Tasks.forException(new Exception("User ID cannot be null"));
        }

        return dbRef.child(USERS_NODE).child(userId).child("equipment").get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    List<Equipment> equipmentList = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Equipment equipment = snapshot.getValue(Equipment.class);
                        if (equipment != null) {
                            equipmentList.add(equipment);
                        }
                    }
                    return equipmentList;
                });
    }

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



    public Task<Void> addMetric(String userId, Metric metric) {
        String metricId = dbRef.child(METRICS_NODE).child(userId).push().getKey();
        return dbRef.child(METRICS_NODE).child(userId).child(metricId).setValue(metric);
    }

    public void getUserMetrics(String userId, ValueEventListener listener) {
        dbRef.child(METRICS_NODE).child(userId).addValueEventListener(listener);
    }

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

    public interface OnMetricsLoadedListener {
        void onMetricsLoaded(List<Metric> metrics);
        void onError(String error);
    }
}