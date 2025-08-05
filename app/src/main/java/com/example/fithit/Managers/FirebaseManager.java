package com.example.fithit.Managers;

import android.util.Log;

import com.example.fithit.Models.Challenge;
import com.example.fithit.Models.ChallengeRecord;
import com.example.fithit.Models.Equipment;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final DatabaseReference dbRef;
    private final FirebaseAuth mAuth;
    private static final String USERS_NODE = "users";
    private static final String USER_CHALLENGES_NODE = "userChallenges";

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
                        throw Objects.requireNonNull(task.getException());
                    }
                    DataSnapshot dataSnapshot = task.getResult();
                    User user = createUserFromSnapshot(dataSnapshot);
                    if (user == null) {
                        throw new Exception("Failed to parse user data");
                    }

                    if (user.getLevel() <= 0) {
                        //
                    }

                    if (user.getCurrentDifficulty() == null) {
                        user.setCurrentDifficulty(DifficultyLevel.BEGINNER);
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
            Boolean wantReminders = snapshot.child("wantReminders").getValue(Boolean.class);
            Integer totalHearts = snapshot.child("totalHearts").getValue(Integer.class);

            if (email == null || username == null || phone == null ||
                    age == null ||  wantReminders == null) {
                return null;
            }

            User user = new User(username, phone, age, wantReminders);

            if (totalHearts != null) {
                user.addHearts(totalHearts);
            }

            DataSnapshot workoutHistorySnapshot = snapshot.child("workoutHistory");
            if (workoutHistorySnapshot.exists()) {
                List<WorkoutRecord> workoutHistory = new ArrayList<>();
                for (DataSnapshot workoutSnapshot : workoutHistorySnapshot.getChildren()) {
                    try {
                        Boolean completed = workoutSnapshot.child("completed").getValue(Boolean.class);

                        Long dateTimestamp = workoutSnapshot.child("date").getValue(Long.class);

                        DataSnapshot workoutDataSnapshot = workoutSnapshot.child("workout");
                        Workout workout = workoutDataSnapshot.getValue(Workout.class);

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
                        // Handle parsing errors
                    }
                }
                user.setWorkoutHistory(workoutHistory);
            }

            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public Task<List<String>> getUserEquipmentIds(String userId) {
        if (userId == null) {
            return Tasks.forException(new Exception("User ID cannot be null"));
        }

        return dbRef.child(USERS_NODE).child(userId).child("equipment").get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                        throw Objects.requireNonNull(task.getException());
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


        return recordRef.setValue(recordData)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseManager", "Workout record saved successfully"))
                .addOnFailureListener(e -> Log.e("FirebaseManager", "Failed to save workout record", e));
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

        Map<String, Object> updates = new HashMap<>();

        updates.put("equipment", null);

        return dbRef.child(USERS_NODE).child(userId).updateChildren(updates)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    Map<String, Object> equipmentUpdates = new HashMap<>();

                    for (Equipment equipment : selectedEquipment) {
                        equipmentUpdates.put(equipment.getDisplayName(), true);
                    }

                    if (equipmentUpdates.isEmpty()) {
                        return Tasks.forResult(null);
                    }

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

    public Task<List<ChallengeRecord>> getUserChallengeRecords() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        return dbRef.child(USERS_NODE)
                .child(userId)
                .child(USER_CHALLENGES_NODE)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    List<ChallengeRecord> records = new ArrayList<>();
                    DataSnapshot dataSnapshot = task.getResult();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                            if (data != null) {
                                Map<String, Object> challengeData = (Map<String, Object>) data.get("challenge");
                                if (challengeData != null) {
                                    Challenge challenge = new Challenge();

                                    challenge.setChallengeId(getIntValue(challengeData, "challengeId"));
                                    challenge.setName(getStringValue(challengeData, "name"));
                                    challenge.setDescription(getStringValue(challengeData, "description"));
                                    challenge.setType(getStringValue(challengeData, "type"));
                                    challenge.setDifficulty(getStringValue(challengeData, "difficulty"));
                                    challenge.setHeartsReward(getIntValue(challengeData, "heartsReward"));
                                    challenge.setStartDate(getLongValue(challengeData, "startDate"));
                                    challenge.setEndDate(getLongValue(challengeData, "endDate"));
                                    challenge.setTargetValue(getIntValue(challengeData, "targetValue"));
                                    challenge.setCompleted(getBooleanValue(challengeData, "completed"));
                                    challenge.setCurrentProgress(getIntValue(challengeData, "currentProgress"));

                                    ChallengeRecord record = new ChallengeRecord();
                                    record.setChallenge(challenge);

                                    record.setDateAdded(getLongValue(data, "dateAdded"));
                                    record.setStartDate(getLongValue(data, "startDate"));
                                    record.setEndDate(getLongValue(data, "endDate"));
                                    record.setCurrentProgress(getIntValue(data, "currentProgress"));
                                    record.setCompleted(getBooleanValue(data, "isCompleted"));

                                    records.add(record);
                                }
                            }
                        } catch (Exception e) {
                            //
                        }
                    }

                    return records;
                });
    }

    private int getIntValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    private long getLongValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : "";
    }

    private boolean getBooleanValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }
    public Task<Void> addChallengeRecord(ChallengeRecord record) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        DatabaseReference recordRef = dbRef.child(USERS_NODE)
                .child(userId)
                .child(USER_CHALLENGES_NODE)
                .push();

        Map<String, Object> recordData = new HashMap<>();
        recordData.put("dateAdded", record.getDateAdded());
        recordData.put("startDate", record.getStartDate());
        recordData.put("endDate", record.getEndDate());
        recordData.put("currentProgress", record.getCurrentProgress());
        recordData.put("isCompleted", record.isCompleted());

        if (record.getChallenge() != null) {
            Map<String, Object> challengeData = new HashMap<>();
            Challenge challenge = record.getChallenge();
            challengeData.put("challengeId", challenge.getChallengeId());
            challengeData.put("name", challenge.getName());
            challengeData.put("description", challenge.getDescription());
            challengeData.put("type", challenge.getType());
            challengeData.put("difficulty", challenge.getDifficulty());
            challengeData.put("heartsReward", challenge.getHeartsReward());
            challengeData.put("startDate", challenge.getStartDate().getTime());
            challengeData.put("endDate", challenge.getEndDate().getTime());
            challengeData.put("targetValue", challenge.getTargetValue());
            challengeData.put("completed", challenge.isCompleted());
            challengeData.put("currentProgress", challenge.getCurrentProgress());

            recordData.put("challenge", challengeData);
        }

        return recordRef.setValue(recordData);
    }
    public Task<Void> updateChallengeRecord(ChallengeRecord record) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        return dbRef.child(USERS_NODE)
                .child(userId)
                .child(USER_CHALLENGES_NODE)
                .orderByChild("challenge/challengeId")
                .equalTo(record.getChallenge().getChallengeId())
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.getChildrenCount() == 0) {
                        throw new Exception("Challenge record not found");
                    }

                    String recordKey = dataSnapshot.getChildren().iterator().next().getKey();
                    assert recordKey != null;
                    return dbRef.child(USERS_NODE)
                            .child(userId)
                            .child(USER_CHALLENGES_NODE)
                            .child(recordKey)
                            .setValue(record);
                });
    }

    public Task<Void> removeChallengeRecord(ChallengeRecord record) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        if (record == null || record.getChallenge() == null) {
            return Tasks.forException(new Exception("Invalid challenge record"));
        }

        final String challengeNameToRemove = record.getChallenge().getName();

        return dbRef.child(USERS_NODE)
                .child(userId)
                .child(USER_CHALLENGES_NODE)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    DataSnapshot dataSnapshot = task.getResult();
                    String recordKeyToRemove = null;

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        try {
                            Map<String, Object> data = (Map<String, Object>) childSnapshot.getValue();
                            if (data != null) {
                                Map<String, Object> challengeData = (Map<String, Object>) data.get("challenge");
                                if (challengeData != null) {
                                    String storedChallengeName = (String) challengeData.get("name");

                                    if (challengeNameToRemove.equals(storedChallengeName)) {
                                        recordKeyToRemove = childSnapshot.getKey();
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            //
                        }
                    }

                    if (recordKeyToRemove != null) {
                        return dbRef.child(USERS_NODE)
                                .child(userId)
                                .child(USER_CHALLENGES_NODE)
                                .child(recordKeyToRemove)
                                .removeValue()
                                .addOnSuccessListener(aVoid ->
                                        Log.d("ChallengeRemoval", "Challenge removed successfully"))
                                .addOnFailureListener(e ->
                                        Log.e("ChallengeRemoval", "Failed to remove challenge", e));
                    } else {
                        Log.e("ChallengeRemoval", "No matching challenge record found");
                        return Tasks.forResult(null);
                    }
                });
    }

    public Task<Void> updateUserData(User user) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user is currently logged in"));
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("totalHearts", user.getTotalHearts());

        return dbRef.child(USERS_NODE)
                .child(userId)
                .updateChildren(updates);
    }

    public void removeWorkoutsByDateListener(ValueEventListener listener) {
        String userId = getCurrentUserId();
        if (userId != null && listener != null) {
            // We don't have the exact query here, so create a similar reference pattern
            dbRef.child(USERS_NODE)
                    .child(userId)
                    .child("workoutHistory")
                    .removeEventListener(listener);
        }
    }

}