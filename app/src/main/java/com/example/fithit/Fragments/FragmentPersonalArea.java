package com.example.fithit.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private TextView tvUserLevel;
    private TextView tvDifficultyLevel;
    private TextView tvHeartsProgress;
    private ProgressBar progressLevel;
    private Button btnBackToMain;
    private MaterialButton btnAddEquipment;

    private RecyclerView challengesRecyclerView;
    private TextView tvNoChallenges;
    private MaterialButton btnAddChallenge;

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
        tvUserLevel = rootView.findViewById(R.id.tv_user_level);
        tvHeartsProgress = rootView.findViewById(R.id.tv_hearts_progress);
        progressLevel = rootView.findViewById(R.id.progress_level);

        tvDifficultyLevel = tvUserLevel;
        tvHeartsProgress = tvUserLevel;

        challengesRecyclerView = rootView.findViewById(R.id.rv_active_challenges);
        tvNoChallenges = rootView.findViewById(R.id.tv_no_challenges);
        btnAddChallenge = rootView.findViewById(R.id.btn_add_challenge);
        btnAddChallenge.setOnClickListener(v -> showChallengesDialog());

        btnBackToMain = rootView.findViewById(R.id.btn_back_to_main);
        btnBackToMain.setOnClickListener(v -> navigateToMainArea());

        btnAddEquipment = rootView.findViewById(R.id.btn_add_equipment);
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
        firebaseManager.getUserChallengeRecords()  // Change to use ChallengeRecord objects
                .addOnSuccessListener(records -> {
                    if (!isAdded()) return;

                    if (records.isEmpty()) {
                        challengesRecyclerView.setVisibility(View.GONE);
                        tvNoChallenges.setVisibility(View.VISIBLE);
                    } else {
                        challengesRecyclerView.setVisibility(View.VISIBLE);
                        tvNoChallenges.setVisibility(View.GONE);

                        UserChallengeRecordAdapter userChallengeAdapter = new UserChallengeRecordAdapter(records);
                        userChallengeAdapter.setOnChallengeActionListener(new UserChallengeRecordAdapter.OnChallengeActionListener() {
                            @Override
                            public void onChallengeRemove(ChallengeRecord record) {
                                firebaseManager.removeChallengeRecord(record)
                                        .addOnSuccessListener(aVoid -> {
                                            if (!isAdded()) return;

                                            // Reload challenges to ensure UI is in sync with database
                                            loadUserChallenges();

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

        // Add content description for the chart
        progressChart.setContentDescription("Workout progress chart showing history of completed workouts");

        // Make chart accessible with custom announcements
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


        // Load user equipment
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

        // Update progress bar
        progressLevel.setProgress(progressPercent);
        progressLevel.setContentDescription(
                "Heart progress: " + progressPercent + "% complete, " + heartsNeeded + " hearts remaining, " +
                        completedWorkouts + " of " + totalWorkouts + " workouts completed");

        // Update workout history chart
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
}