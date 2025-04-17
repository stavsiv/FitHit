package com.example.fithit.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Adapters.EquipmentAdapter;
import com.example.fithit.Adapters.UserChallengeRecordAdapter;
import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Models.ChallengeRecord;
import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;
import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.User;
import com.example.fithit.Managers.FirebaseManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.button.MaterialButton;
import com.example.fithit.Managers.ChallengeProgressManager;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.List;

public class FragmentPersonalArea extends Fragment {

    private static final int HEARTS_FOR_INTERMEDIATE = 100;
    private static final int HEARTS_FOR_EXPERT = 250;
    private FirebaseManager firebaseManager;
    private View rootView;
    private RecyclerView equipmentRecyclerView;
    private LineChart progressChart;
    private EquipmentAdapter equipmentAdapter;
    private TextView tvUserName;
    private TextView tvDifficultyLevel;
    private TextView tvHeartsProgress;
    private ProgressBar progressLevel;

    private RecyclerView challengesRecyclerView;
    private TextView tvNoChallenges;

    public static FragmentPersonalArea newInstance() {
        return new FragmentPersonalArea();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personal_area_dashboard, container, false);
        initializeViews();
        setupRecyclerViews();
        setupChart();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserData();
        loadUserChallenges();    }

    private void initializeViews() {
        equipmentRecyclerView = rootView.findViewById(R.id.rv_equipment);
        progressChart = rootView.findViewById(R.id.chart_progress);
        tvUserName = rootView.findViewById(R.id.tv_user_name);
        TextView tvUserLevel = rootView.findViewById(R.id.tv_user_level);
        tvHeartsProgress = rootView.findViewById(R.id.tv_hearts_progress);
        progressLevel = rootView.findViewById(R.id.progress_level);

        tvDifficultyLevel = tvUserLevel;
        tvHeartsProgress = tvUserLevel;

        challengesRecyclerView = rootView.findViewById(R.id.rv_active_challenges);
        tvNoChallenges = rootView.findViewById(R.id.tv_no_challenges);
        MaterialButton btnAddChallenge = rootView.findViewById(R.id.btn_add_challenge);
        btnAddChallenge.setOnClickListener(v -> showChallengesDialog());

        Button btnBackToMain = rootView.findViewById(R.id.btn_back_to_main);
        btnBackToMain.setOnClickListener(v -> navigateToMainArea());

        MaterialButton btnAddEquipment = rootView.findViewById(R.id.btn_add_equipment);
        btnAddEquipment.setOnClickListener(v -> showEquipmentSelectionDialog());
    }


    private void showChallengesDialog() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        firebaseManager.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    if (!isAdded()) return;

                    AvailableChallengesDialog dialog = new AvailableChallengesDialog(user);
                    dialog.setOnChallengeSelectedListener(challenge -> {
                        ChallengeProgressManager.getInstance().addChallengeForUser(challenge);
                        loadUserChallenges();
                        Toast.makeText(requireContext(),
                                "Challenge added successfully",
                                Toast.LENGTH_SHORT).show();
                    });
                    dialog.show(getChildFragmentManager(), "available_challenges_dialog");
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "Error loading user details: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserChallenges() {
        firebaseManager.getUserChallengeRecords()
                .addOnSuccessListener(records -> {
                    if (!isAdded()) return;

                    List<ChallengeRecord> activeRecords = new ArrayList<>();
                    for (ChallengeRecord record : records) {
                        if (!record.isCompleted()) {
                            activeRecords.add(record);
                        }
                    }

                    if (activeRecords.isEmpty()) {
                        challengesRecyclerView.setVisibility(View.GONE);
                        tvNoChallenges.setVisibility(View.VISIBLE);
                    } else {
                        challengesRecyclerView.setVisibility(View.VISIBLE);
                        tvNoChallenges.setVisibility(View.GONE);

                        UserChallengeRecordAdapter userChallengeAdapter = new UserChallengeRecordAdapter(activeRecords);

                        userChallengeAdapter.setOnListEmptyListener(() -> {
                            challengesRecyclerView.setVisibility(View.GONE);
                            tvNoChallenges.setVisibility(View.VISIBLE);
                        });

                        userChallengeAdapter.setOnChallengeActionListener(new UserChallengeRecordAdapter.OnChallengeActionListener() {
                            @Override
                            public void onChallengeRemove(ChallengeRecord record) {
                                firebaseManager.removeChallengeRecord(record)
                                        .addOnSuccessListener(aVoid -> {
                                            if (!isAdded()) return;

                                            userChallengeAdapter.removeCompletedChallenge(record);

                                            Toast.makeText(requireContext(),
                                                    "Challenge removed successfully",
                                                    Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            if (!isAdded()) return;
                                            Toast.makeText(requireContext(),
                                                    "Error removing challenge: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            }

                            @Override
                            public void onChallengeRenew(ChallengeRecord record) {
                                ChallengeProgressManager.getInstance().renewChallenge(record);
                                loadUserChallenges();
                            }

                            @Override
                            public void onChallengeCompleted(ChallengeRecord record) {
                                Log.d("ChallengeComplete", "onChallengeCompleted called for: " + record.getChallengeName());

                                record.setCompleted(true);

                                String userId = firebaseManager.getCurrentUserId();
                                if (userId == null) {
                                    Log.e("ChallengeComplete", "No user logged in");
                                    return;
                                }

                                com.google.firebase.database.DatabaseReference userChallengesRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(userId).child("userChallenges");

                                userChallengesRef.get().addOnSuccessListener(dataSnapshot -> {
                                    String recordKeyToUpdate = null;

                                    for (com.google.firebase.database.DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        try {
                                            java.util.Map<String, Object> data = (java.util.Map<String, Object>) childSnapshot.getValue();
                                            if (data != null) {
                                                java.util.Map<String, Object> challengeData = (java.util.Map<String, Object>) data.get("challenge");
                                                if (challengeData != null) {
                                                    String storedChallengeName = (String) challengeData.get("name");

                                                    if (storedChallengeName != null &&
                                                            storedChallengeName.equals(record.getChallenge().getName())) {
                                                        recordKeyToUpdate = childSnapshot.getKey();
                                                        break;
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.e("ChallengeComplete", "Error finding challenge: " + e.getMessage());
                                        }
                                    }

                                    if (recordKeyToUpdate != null) {
                                        Log.d("ChallengeComplete", "Found challenge key to update: " + recordKeyToUpdate);

                                        java.util.Map<String, Object> updates = new java.util.HashMap<>();
                                        updates.put("currentProgress", record.getCurrentProgress());
                                        updates.put("isCompleted", true);
                                        updates.put("challenge/completed", true);
                                        updates.put("challenge/currentProgress", record.getCurrentProgress());

                                        final String finalRecordKey = recordKeyToUpdate;
                                        userChallengesRef.child(recordKeyToUpdate).updateChildren(updates)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("ChallengeComplete", "Challenge updated successfully");

                                                    firebaseManager.getCurrentUserData()
                                                            .addOnSuccessListener(user -> {
                                                                if (user != null) {
                                                                    int heartsReward = record.getChallenge().getHeartsReward();
                                                                    user.addHearts(heartsReward);
                                                                    firebaseManager.updateUserData(user)
                                                                            .addOnSuccessListener(aVoid2 -> {
                                                                                if (!isAdded())
                                                                                    return;

                                                                                Toast.makeText(requireContext(),
                                                                                        "Challenge completed! You earned " + heartsReward + " hearts!",
                                                                                        Toast.LENGTH_LONG).show();

                                                                                userChallengeAdapter.removeCompletedChallenge(record);
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                if (!isAdded())
                                                                                    return;
                                                                                Log.e("ChallengeComplete", "Failed to update user hearts: " + e.getMessage());
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                if (!isAdded()) return;
                                                                Log.e("ChallengeComplete", "Failed to get user data: " + e.getMessage());
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    if (!isAdded()) return;
                                                    Log.e("ChallengeComplete", "Failed to update challenge: " + e.getMessage());
                                                    Toast.makeText(requireContext(),
                                                            "Failed to update challenge: " + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        if (!isAdded()) return;
                                        Log.e("ChallengeComplete", "Challenge not found for update");
                                        Toast.makeText(requireContext(), "Challenge not found", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    if (!isAdded()) return;
                                    Log.e("ChallengeComplete", "Error getting challenges: " + e.getMessage());
                                    Toast.makeText(requireContext(),
                                            "Error getting challenges: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        });

                        challengesRecyclerView.setAdapter(userChallengeAdapter);
                        challengesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "Error loading challenges: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        ChallengeProgressManager.getInstance().updateAllChallengesProgress();
        loadUserChallenges();
    }
    private void navigateToMainArea() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void setupRecyclerViews() {
        equipmentAdapter = new EquipmentAdapter((equipment, isSelected) -> {
            updateEquipmentInFirebase(equipment, isSelected);

            String announcement = isSelected ?
                    "Added " + equipment.getDisplayName() + " to your equipment" :
                    "Removed " + equipment.getDisplayName() + " from your equipment";

            announceForAccessibility(announcement);
        });

        equipmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        equipmentRecyclerView.setAdapter(equipmentAdapter);
    }

    private void updateProgressChart(List<WorkoutRecord> history) {
        if (history.isEmpty()) return;

        ArrayList<Entry> entries = new ArrayList<>();

        int totalWorkouts = history.size();

        for (int i = 0; i < history.size(); i++) {
            entries.add(new Entry(i, i+1));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Workout Progress");
        dataSet.setDrawFilled(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        progressChart.setData(lineData);
        progressChart.getDescription().setEnabled(false);
        progressChart.getXAxis().setEnabled(false);
        progressChart.invalidate();
    }

    private void setupChart() {
        progressChart.setTouchEnabled(true);
        progressChart.setDragEnabled(true);
        progressChart.setScaleEnabled(true);
        progressChart.setPinchZoom(false);
        progressChart.getAxisLeft().setDrawGridLines(false);
        progressChart.getAxisRight().setEnabled(false);
        progressChart.getLegend().setEnabled(false);

        progressChart.setContentDescription("Workout progress chart showing history of completed workouts");

        progressChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String announcement = "Workout " + ((int)e.getX() + 1) +
                        ": Total " + (int)e.getY() + " workouts completed";
                announceForAccessibility(announcement);
            }

            @Override
            public void onNothingSelected() {
                // Optional: Announce when selection is cleared
            }
        });
    }

    private void announceForAccessibility(String announcement) {
        if (getView() != null) {
            getView().announceForAccessibility(announcement);
        }
    }

    private void loadUserData() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseManager.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    if (!isAdded()) return;

                    updateUserProfileUI(user);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    if (e.getMessage() != null && e.getMessage().contains("Permission denied")) {
                        Toast.makeText(requireContext(),
                                "Refreshing login to solve permission issues...",
                                Toast.LENGTH_LONG).show();

                        FirebaseAuth.getInstance().signOut();

                        navigateToLoginScreen();
                    } else {
                        Toast.makeText(requireContext(),
                                "Failed to load user data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });


        firebaseManager.getUserEquipmentIds(userId)
                .addOnSuccessListener(equipmentNames -> {
                    if (!isAdded()) return;
                    equipmentAdapter.updateEquipmentSelection(equipmentNames);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "Failed to load equipment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(requireActivity(), FragmentLogin.class);
        startActivity(intent);
        requireActivity().finish();
    }
    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    private void updateUserProfileUI(User user) {
        if (user == null || !isAdded()) return;

        String userName = user.getUserName();
        tvUserName.setText(userName);
        tvUserName.setContentDescription("User profile for " + userName);

        DifficultyLevel difficulty = user.getCurrentDifficulty();
        int hearts = user.getTotalHearts();
        int heartsNeeded = 0;

        if (difficulty == DifficultyLevel.BEGINNER) {
            heartsNeeded = HEARTS_FOR_INTERMEDIATE - hearts;
        } else if (difficulty == DifficultyLevel.INTERMEDIATE) {
            heartsNeeded = HEARTS_FOR_EXPERT - hearts;
        }

        tvDifficultyLevel.setText("Difficulty: " + difficulty.toString());

        int totalWorkouts = user.getTotalWorkouts();
        int completedWorkouts = user.getCompletedWorkoutsCount();
        if (heartsNeeded > 0) {
            tvHeartsProgress.setText("Hearts : " + hearts + " (" + heartsNeeded + " more for next level)\n" +
                    "Completed : " + completedWorkouts + " / " + totalWorkouts + " workouts");
        } else {
            tvHeartsProgress.setText("Hearts : " + hearts + " (Max level reached)\n" +
                    "Completed : " + completedWorkouts + " / " + totalWorkouts + " workouts");
        }

        int maxHearts = (difficulty == DifficultyLevel.BEGINNER) ? HEARTS_FOR_INTERMEDIATE :
                (difficulty == DifficultyLevel.INTERMEDIATE) ? HEARTS_FOR_EXPERT : hearts;
        int progressPercent = (hearts * 100) / maxHearts;

        progressLevel.setProgress(progressPercent);
        progressLevel.setContentDescription(
                "Heart progress: " + progressPercent + "% complete, " + heartsNeeded + " hearts remaining, " +
                        completedWorkouts + " of " + totalWorkouts + " workouts completed");

        List<WorkoutRecord> workoutHistory = user.getWorkoutHistory();
        updateProgressChart(workoutHistory);
    }

    private void updateEquipmentInFirebase(Equipment equipment, boolean isSelected) {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        if (isSelected) {
            firebaseManager.addOrUpdateUserEquipment(userId, equipment)
                    .addOnSuccessListener(aVoid -> {
                        if (!isAdded()) return;
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(),
                                "Failed to add equipment: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            firebaseManager.removeEquipmentFromUser(userId, equipment.getDisplayName())
                    .addOnSuccessListener(aVoid -> {
                        if (!isAdded()) return;
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(),
                                "Failed to remove equipment: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveAllEquipment() {
        if (!isAdded()) return;

        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;
        List<Equipment> selectedEquipment = equipmentAdapter.getSelectedEquipment();

        firebaseManager.updateUserEquipmentBatch(userId, selectedEquipment)
                .addOnSuccessListener(aVoid -> {
                    if (!isAdded()) return;
                    // loadingDialog.dismiss();
                    Toast.makeText(requireContext(),
                            "Equipment updated successfully",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    // loadingDialog.dismiss();
                    Toast.makeText(requireContext(),
                            "Failed to update equipment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showEquipmentSelectionDialog() {
        if (!isAdded()) return;

        saveAllEquipment();
    }

    private void showSimpleUpdateDialog(ChallengeRecord record) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("progress update: " + record.getChallenge().getName());

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(record.getCurrentProgress()));
        builder.setView(input);

        builder.setPositiveButton("update", (dialog, which) -> {
            try {
                int newProgress = Integer.parseInt(input.getText().toString());
                if (newProgress >= 0) {
                    record.updateProgress(newProgress);

                    firebaseManager.updateChallengeRecord(record)
                            .addOnSuccessListener(aVoid -> {
                                loadUserChallenges();
                                Toast.makeText(getContext(),
                                        "The progress was updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(),
                                        "Error updating progress: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(getContext(), "Please enter a positive number", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}