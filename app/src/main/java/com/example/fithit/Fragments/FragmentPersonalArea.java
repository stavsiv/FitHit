package com.example.fithit.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
//import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fithit.Managers.WorkoutChartManager;
import com.github.mikephil.charting.charts.CombinedChart;

import com.example.fithit.Adapters.EquipmentAdapter;
import com.example.fithit.Adapters.UserChallengeRecordAdapter;
import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Models.ChallengeRecord;
import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;
import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.User;
import com.example.fithit.Managers.FirebaseManager;
import com.google.android.material.button.MaterialButton;
import com.example.fithit.Managers.ChallengeProgressManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentPersonalArea extends Fragment {

    private static final int HEARTS_FOR_INTERMEDIATE = 100;
    private static final int HEARTS_FOR_EXPERT = 250;
    private FirebaseManager firebaseManager;
    private View rootView;
    private RecyclerView equipmentRecyclerView;
    public CombinedChart progressChart;
    private EquipmentAdapter equipmentAdapter;
    private TextView tvUserName;
    private TextView tvDifficultyLevel;
    private TextView tvHeartsProgress;
    //private ProgressBar progressLevel;
    private RecyclerView challengesRecyclerView;
    private TextView tvNoChallenges;
    private WorkoutChartManager chartManager;

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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserData();
        loadUserChallenges();
    }

    private void initializeViews() {
        equipmentRecyclerView = rootView.findViewById(R.id.rv_equipment);
        progressChart = rootView.findViewById(R.id.chart_progress);
        chartManager = new WorkoutChartManager(progressChart, requireContext());
        tvUserName = rootView.findViewById(R.id.tv_user_name);
        TextView tvUserLevel = rootView.findViewById(R.id.tv_user_level);
        tvHeartsProgress = rootView.findViewById(R.id.tv_hearts_progress);

        tvDifficultyLevel = tvUserLevel;

        challengesRecyclerView = rootView.findViewById(R.id.rv_active_challenges);
        tvNoChallenges = rootView.findViewById(R.id.tv_no_challenges);

        MaterialButton btnAddChallenge = rootView.findViewById(R.id.btn_add_challenge);
        btnAddChallenge.setOnClickListener(v -> showChallengesDialog());

        Button btnBackToMain = rootView.findViewById(R.id.btn_back_to_main);
        btnBackToMain.setOnClickListener(v -> navigateToMainArea());

        MaterialButton btnLogout = rootView.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        MaterialButton btnAddEquipment = rootView.findViewById(R.id.btn_add_equipment);
        btnAddEquipment.setOnClickListener(v -> showEquipmentSelectionDialog());

        chartManager.setOnChartAnnouncementListener(this::announceForAccessibility);
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
                        Toast.makeText(requireContext(), R.string.challenge_added_successfully,
                                Toast.LENGTH_SHORT).show();
                    });
                    dialog.show(getChildFragmentManager(), "available_challenges_dialog");
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), getString(R.string.error_loading_user_details) + e.getMessage(),
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
                                            Toast.makeText(requireContext(), R.string.challenge_removed_successfully,
                                                    Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            if (!isAdded()) return;
                                            Toast.makeText(requireContext(), getString(R.string.error_removing_challenge) + e.getMessage(),
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
                                record.setCompleted(true);
                                userChallengeAdapter.removeCompletedChallenge(record);

                                int heartsReward = record.getChallenge().getHeartsReward();
                                Toast.makeText(requireContext(),
                                        getString(R.string.challenge_completed_you_earned) + " " + heartsReward + " " + getString(R.string.hearts),
                                        Toast.LENGTH_LONG).show();

                                updateChallengeInFirebase(record, heartsReward);
                            }
                        });

                        challengesRecyclerView.setAdapter(userChallengeAdapter);
                        challengesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), getString(R.string.error_loading_challenges) + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateChallengeInFirebase(ChallengeRecord record, int heartsReward) {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        DatabaseReference userChallengesRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("userChallenges");

        userChallengesRef.get().addOnSuccessListener(dataSnapshot -> {
            String recordKeyToUpdate = null;

            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                try {
                    Map<String, Object> data = (Map<String, Object>) childSnapshot.getValue();
                    if (data != null) {
                        Map<String, Object> challengeData = (Map<String, Object>) data.get("challenge");
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
                    Log.e("PersonalArea", "Error processing challenge data", e);
                }
            }

            if (recordKeyToUpdate != null) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("currentProgress", record.getCurrentProgress());
                updates.put("isCompleted", true);
                updates.put("challenge/completed", true);
                updates.put("challenge/currentProgress", record.getCurrentProgress());
                updates.put("completedAt", System.currentTimeMillis());

                userChallengesRef.child(recordKeyToUpdate).updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            updateUserHearts(heartsReward);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("PersonalArea", "Failed to update challenge in Firebase", e);
                        });
            }
        }).addOnFailureListener(e -> {
            Log.e("PersonalArea", "Failed to get challenges from Firebase", e);
        });
    }

    private void updateUserHearts(int heartsReward) {
        firebaseManager.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    if (user != null) {
                        DifficultyLevel previousLevel = user.getCurrentDifficulty();
                        user.addHearts(heartsReward);

                        firebaseManager.updateUserData(user)
                                .addOnSuccessListener(aVoid2 -> {
                                    if (!isAdded()) return;

                                    DifficultyLevel newLevel = user.getCurrentDifficulty();
                                    boolean leveledUp = !previousLevel.equals(newLevel);

                                    if (leveledUp) {
                                        String levelUpMessage = "🎉 " + getString(R.string.level_up) + "! " +
                                                previousLevel.toString() + " → " + newLevel.toString();
                                        Toast.makeText(requireContext(), levelUpMessage, Toast.LENGTH_LONG).show();
                                    }
                                    loadUserData();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("PersonalArea", "Failed to update user hearts", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonalArea", "Failed to load user for hearts update", e);
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
                    getString(R.string.added) + equipment.getDisplayName() + getString(R.string.to_your_equipment) :
                    getString(R.string.removed) + equipment.getDisplayName() + getString(R.string.from_your_equipment);

            announceForAccessibility(announcement);
        });

        equipmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        equipmentRecyclerView.setAdapter(equipmentAdapter);
    }

    private void announceForAccessibility(String announcement) {
        if (getView() != null) {
            getView().announceForAccessibility(announcement);
        }
    }

    private void loadUserData() {
        String userId = firebaseManager.getCurrentUserId();

        if (userId == null) {
            Log.e("PersonalArea", "User ID is null");
            Toast.makeText(requireContext(),
                    R.string.please_log_in_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseManager.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    if (!isAdded()) return;
                    updateUserProfileUI(user);
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonalArea", "Failed to load user data: " + e.getMessage(), e);
                    if (!isAdded()) return;
                    if (e.getMessage() != null && e.getMessage().contains(getString(R.string.permission_denied))) {
                        Toast.makeText(requireContext(), R.string.refreshing_login_to_solve_permission_issues,
                                Toast.LENGTH_LONG).show();

                        FirebaseAuth.getInstance().signOut();
                        navigateToLoginScreen();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.failed_to_load_user_data) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        firebaseManager.getUserEquipmentIds(userId)
                .addOnSuccessListener(equipmentNames -> {
                    if (!isAdded()) return;
                    equipmentAdapter.updateEquipmentSelection(equipmentNames);
                })
                .addOnFailureListener(e -> {
                    Log.e("PersonalArea", "Failed to load equipment: " + e.getMessage(), e);
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), getString(R.string.failed_to_load_equipment) + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToLoginScreen() {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentLogin loginFragment = new FragmentLogin();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, loginFragment)
                    .commit();
        }
    }
    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    private void updateUserProfileUI(User user) {
        if (user == null || !isAdded()) return;

        String userName = user.getUserName();
        tvUserName.setText(userName);
        tvUserName.setContentDescription(getString(R.string.user_profile_for) + userName);

        DifficultyLevel difficulty = user.getCurrentDifficulty();
        int hearts = user.getTotalHearts();
        int heartsNeeded = 0;

        if (difficulty == DifficultyLevel.BEGINNER) {
            heartsNeeded = HEARTS_FOR_INTERMEDIATE - hearts;
        } else if (difficulty == DifficultyLevel.INTERMEDIATE) {
            heartsNeeded = HEARTS_FOR_EXPERT - hearts;
        }

        tvDifficultyLevel.setText(getString(R.string.difficulty) + difficulty.toString());

        int totalWorkouts = user.getTotalWorkouts();
        int completedWorkouts = user.getCompletedWorkoutsCount();

        String heartsLabel = getString(R.string.hearts);
        String moreForNextLevel = getString(R.string.more_for_next_level);
        String completedLabel = getString(R.string.completed);
        String workoutsLabel = getString(R.string.workouts);
        String maxLevel = getString(R.string.max_level_reached);

        String progressText;
        if (heartsNeeded > 0) {
            progressText = heartsLabel + ": " + hearts + " ( " + heartsNeeded + " " + moreForNextLevel + ")\n" +
                    completedLabel + " " + completedWorkouts + " / " + totalWorkouts + " " + workoutsLabel;
        } else {
            progressText = heartsLabel + ": " + hearts + " (" + maxLevel + ")\n" +
                    completedLabel + " " + completedWorkouts + " / " + totalWorkouts + " " + workoutsLabel;
        }
        tvHeartsProgress.setText(progressText);

        int maxHearts = (difficulty == DifficultyLevel.BEGINNER) ? HEARTS_FOR_INTERMEDIATE :
                (difficulty == DifficultyLevel.INTERMEDIATE) ? HEARTS_FOR_EXPERT : hearts;
        int progressPercent = (hearts * 100) / maxHearts;

        String contentDescription = getString(
                R.string.heart_progress_description,
                progressPercent, heartsNeeded, completedWorkouts, totalWorkouts
        );

        //progressLevel.setContentDescription(contentDescription);

        List<WorkoutRecord> workoutHistory = user.getWorkoutHistory();
        chartManager.updateChart(workoutHistory);
    }

    private void updateEquipmentInFirebase(Equipment equipment, boolean isSelected) {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        if (isSelected) {
            firebaseManager.addOrUpdateUserEquipment(userId, equipment)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), getString(R.string.failed_to_add_equipment) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            firebaseManager.removeEquipmentFromUser(userId, equipment.getDisplayName())
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), getString(R.string.failed_to_remove_equipment) + e.getMessage(),
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
                    Toast.makeText(requireContext(), R.string.equipment_updated_successfully,
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), getString(R.string.failed_to_update_equipment) + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showEquipmentSelectionDialog() {
        if (!isAdded()) return;
        saveAllEquipment();
    }

    //logout
    private void showLogoutConfirmationDialog() {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_confirmation_title)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void performLogout() {
        try {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(requireContext(), R.string.logout_successful, Toast.LENGTH_SHORT).show();

            navigateToLoginScreen();

        } catch (Exception e) {
            Log.e("PersonalArea", "Error during logout: " + e.getMessage(), e);
            if (isAdded()) {
                Toast.makeText(requireContext(), R.string.logout_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}