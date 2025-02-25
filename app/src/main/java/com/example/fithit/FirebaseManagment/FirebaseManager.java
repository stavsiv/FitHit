package com.example.fithit.FirebaseManagment;

import android.util.Log;

import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.Metric;
import com.example.fithit.Models.User;
import com.example.fithit.Models.Workout;
import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Models.WorkoutRecord;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final DatabaseReference dbRef;
    private final FirebaseAuth mAuth;
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
            Integer totalHearts = snapshot.child("totalHearts").getValue(Integer.class);

            if (email == null || username == null || phone == null ||
                    age == null || weight == null || wantReminders == null) {
                return null;
            }

            User user = new User(username, phone, age, wantReminders);

            // Set total hearts
            if (totalHearts != null) {
                user.addHearts(totalHearts);
            }

            // Load workout history
            DataSnapshot workoutHistorySnapshot = snapshot.child("workoutHistory");
            if (workoutHistorySnapshot.exists()) {
                List<WorkoutRecord> workoutHistory = new ArrayList<>();
                for (DataSnapshot workoutSnapshot : workoutHistorySnapshot.getChildren()) {
                    try {
                        // Get completed status
                        Boolean completed = workoutSnapshot.child("completed").getValue(Boolean.class);

                        // Get date
                        Long dateTimestamp = workoutSnapshot.child("date").getValue(Long.class);

                        // Get workout data
                        DataSnapshot workoutDataSnapshot = workoutSnapshot.child("workout");
                        Workout workout = workoutDataSnapshot.getValue(Workout.class);

                        // Create workout record
                        WorkoutRecord record = new WorkoutRecord();
                        if (dateTimestamp != null) {
                            record.setDate(dateTimestamp);
                        }
                        if (workout != null) {
                            record.setWorkout(workout);
                        }
                        if (completed != null) {
                            record.setCompleted(completed);
                        }

                        // Get metrics
                        DataSnapshot metricsSnapshot = workoutSnapshot.child("metrics");
                        if (metricsSnapshot.exists()) {
                            for (DataSnapshot metricSnapshot : metricsSnapshot.getChildren()) {
                                String metricKey = metricSnapshot.getKey();
                                Double metricValue = metricSnapshot.getValue(Double.class);
                                if (metricKey != null && metricValue != null) {
                                    record.addMetric(metricKey, metricValue);
                                }
                            }
                        }

                        workoutHistory.add(record);
                    } catch (Exception e) {
                        Log.e("FirebaseManager", "Error parsing workout record: " + e.getMessage());
                    }
                }
                user.setWorkoutHistory(workoutHistory);
            }

            return user;
        } catch (Exception e) {
            Log.e("FirebaseManager", "Error creating user from snapshot: " + e.getMessage());
            return null;
        }
    }
    /*public User createUserFromSnapshot(DataSnapshot snapshot) {
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

            return new User(username, phone, age, wantReminders);
        } catch (Exception e) {
            return null;
        }
    }*/
    public Task<List<String>> getUserEquipmentIds(String userId) {
        if (userId == null) {
            return Tasks.forException(new Exception("User ID cannot be null"));
        }

        return dbRef.child(USERS_NODE).child(userId).child("equipment").get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    List<String> equipmentIds = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        equipmentIds.add(snapshot.getKey());
                    }
                    return equipmentIds;
                });

    }
    public String getCurrentUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        return null;
    }

    public Task<Void> addOrUpdateUserEquipment(String userId, Equipment equipment) {
        if (userId == null || equipment == null) {
            return Tasks.forException(new Exception("User ID and Equipment cannot be null"));
        }

        DatabaseReference userEquipmentRef = dbRef.child(USERS_NODE).child(userId).child("equipment");

        String equipmentKey = equipment.getDisplayName();

        return userEquipmentRef.child(equipmentKey)
                .setValue(equipment.isSelected() ? true : null);
    }

    public Task<Void> removeEquipmentFromUser(String userId, String equipmentId) {
        if (userId == null || equipmentId == null) {
            return Tasks.forException(new Exception("User ID and Equipment ID cannot be null"));
        }
        return dbRef.child(USERS_NODE).child(userId)
                .child("equipment").child(equipmentId).removeValue();
    }

    public Task<Void> updateUserHearts(int heartsEarned) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        return dbRef.child(USERS_NODE).child(userId).get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    DataSnapshot snapshot = task.getResult();
                    Integer currentHearts = snapshot.child("totalHearts").getValue(Integer.class);
                    int newTotalHearts = (currentHearts != null ? currentHearts : 0) + heartsEarned;

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("totalHearts", newTotalHearts);

                    return dbRef.child(USERS_NODE).child(userId).updateChildren(updates);
                });
    }
    public Task<Void> addWorkoutRecord(WorkoutRecord workoutRecord) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e("FirebaseManager", "No user logged in");
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        DatabaseReference recordRef = dbRef.child(USERS_NODE)
                .child(userId)
                .child("workoutHistory")
                .push();

        Map<String, Object> recordData = new HashMap<>();
        recordData.put("workout", workoutRecord.getWorkout());
        recordData.put("date", workoutRecord.getDate());
        recordData.put("completed", workoutRecord.isCompleted());
        recordData.put("metrics", workoutRecord.getMetrics());
        recordData.put("workoutDateTime", new Date().getTime());

        Log.d("FirebaseManager", "Saving workout record: " + recordData.toString());

        return recordRef.setValue(recordData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseManager", "Workout record saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseManager", "Failed to save workout record", e);
                });
    }
    public Task<Integer> getWeeklyWorkoutsCount() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        long weekStart = getStartOfWeek();

        return dbRef.child(USERS_NODE)
                .child(userId)
                .child("workoutHistory")
                .orderByChild("date")
                .startAt(weekStart)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return (int) task.getResult().getChildrenCount();
                });
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
    public void getWorkoutsByDate(Date date, ValueEventListener listener) {
        String userId = getCurrentUserId();
        if (userId != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startOfDay = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            long endOfDay = calendar.getTimeInMillis();

            dbRef.child(USERS_NODE)
                    .child(userId)
                    .child("workoutHistory")
                    .orderByChild("date")
                    .startAt(startOfDay)
                    .endAt(endOfDay)
                    .addValueEventListener(listener);
        }
    }

    public Task<Void> updateUserEquipmentBatch(String userId, List<Equipment> selectedEquipment) {
        if (userId == null) {
            return Tasks.forException(new Exception("User ID cannot be null"));
        }

        // יצירת מפה של עדכונים
        Map<String, Object> updates = new HashMap<>();

        // ראשית, מוחקים את כל הציוד הקיים על ידי הגדרת הנתיב לנתיב ריק
        updates.put("equipment", null);

        // ביצוע העדכון הראשוני - מחיקת הציוד הקיים
        return dbRef.child(USERS_NODE).child(userId).updateChildren(updates)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // עכשיו נוסיף את הציוד החדש
                    Map<String, Object> equipmentUpdates = new HashMap<>();

                    // מוסיפים את כל פריטי הציוד שנבחרו למפה
                    for (Equipment equipment : selectedEquipment) {
                        equipmentUpdates.put(equipment.getDisplayName(), true);
                    }

                    // אם אין ציוד שנבחר, נחזיר משימה שהושלמה
                    if (equipmentUpdates.isEmpty()) {
                        return Tasks.forResult(null);
                    }

                    // ביצוע העדכון - הוספת כל הציוד החדש
                    return dbRef.child(USERS_NODE).child(userId)
                            .child("equipment").updateChildren(equipmentUpdates);
                });
    }
    public void getUpcomingWorkouts(ValueEventListener listener) {
        String userId = getCurrentUserId();
        if (userId != null) {
            dbRef.child(USERS_NODE)
                    .child(userId)
                    .child("workoutHistory")
                    .orderByChild("date")
                    .startAt(new Date().getTime())
                    .addValueEventListener(listener);
        }
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