package com.example.fithit.Fragments;

import static com.example.fithit.R.string.challenge_not_found;

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

                                String userId = firebaseManager.getCurrentUserId();
                                if (userId == null) {
                                    return;
                                }

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
                                            // Handle exceptions here
                                        }
                                    }

                                    if (recordKeyToUpdate != null) {

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("currentProgress", record.getCurrentProgress());
                                        updates.put("isCompleted", true);
                                        updates.put("challenge/completed", true);
                                        updates.put("challenge/currentProgress", record.getCurrentProgress());

                                        final String finalRecordKey = recordKeyToUpdate;
                                        userChallengesRef.child(recordKeyToUpdate).updateChildren(updates)
                                                .addOnSuccessListener(aVoid -> {

                                                    firebaseManager.getCurrentUserData()
                                                            .addOnSuccessListener(user -> {
                                                                if (user != null) {
                                                                    int heartsReward = record.getChallenge().getHeartsReward();
                                                                    user.addHearts(heartsReward);
                                                                    firebaseManager.updateUserData(user)
                                                                            .addOnSuccessListener(aVoid2 -> {
                                                                                if (!isAdded())
                                                                                    return;

                                                                                Toast.makeText(requireContext(), getString(R.string.challenge_completed_you_earned) + heartsReward + R.string.hearts,
                                                                                        Toast.LENGTH_LONG).show();

                                                                                userChallengeAdapter.removeCompletedChallenge(record);
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                if (!isAdded())
                                                                                    return;
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                if (!isAdded()) return;
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    if (!isAdded()) return;
                                                    Toast.makeText(requireContext(), getString(R.string.failed_to_update_challenge) + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        if (!isAdded()) return;
                                        Toast.makeText(requireContext(), challenge_not_found, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    if (!isAdded()) return;
                                    Toast.makeText(requireContext(), getString(R.string.error_getting_challenges) + e.getMessage(),
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
                    Toast.makeText(requireContext(), getString(R.string.error_loading_challenges) + e.getMessage(),
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
                    getString(R.string.added) + equipment.getDisplayName() + getString(R.string.to_your_equipment) :
                    getString(R.string.removed) + equipment.getDisplayName() + getString(R.string.from_your_equipment);

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

        progressChart.setContentDescription(getString(R.string.workout_progress_chart_showing_history_of_completed_workouts));

        progressChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String announcement = getString(R.string.workout) + ((int)e.getX() + 1) +
                        getString(R.string.total) + (int)e.getY() + getString(R.string.workouts_completed);
                announceForAccessibility(announcement);
            }

            @Override
            public void onNothingSelected() {
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
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), getString(R.string.failed_to_load_equipment) + e.getMessage(),
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

        if (heartsNeeded > 0) {
            String progressText = heartsLabel + ": " + hearts + " (" + heartsNeeded + moreForNextLevel + ")\n" +
                    completedLabel + " " + completedWorkouts + " / " + totalWorkouts + " " + workoutsLabel;
            tvHeartsProgress.setText(progressText);
        } else {
            String progressText = heartsLabel + ": " + hearts + " (" + maxLevel + ")\n" +
                    completedLabel + " " + completedWorkouts + " / " + totalWorkouts + " " + workoutsLabel;
            tvHeartsProgress.setText(progressText);
        }



        int maxHearts = (difficulty == DifficultyLevel.BEGINNER) ? HEARTS_FOR_INTERMEDIATE :
                (difficulty == DifficultyLevel.INTERMEDIATE) ? HEARTS_FOR_EXPERT : hearts;
        int progressPercent = (hearts * 100) / maxHearts;

        String contentDescription = getString(
                R.string.heart_progress_description,
                progressPercent, heartsNeeded, completedWorkouts, totalWorkouts
        );

        progressLevel.setContentDescription(contentDescription);

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
                        Toast.makeText(requireContext(), getString(R.string.failed_to_add_equipment) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            firebaseManager.removeEquipmentFromUser(userId, equipment.getDisplayName())
                    .addOnSuccessListener(aVoid -> {
                        if (!isAdded()) return;
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
                    // loadingDialog.dismiss();
                    Toast.makeText(requireContext(), R.string.equipment_updated_successfully,
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    // loadingDialog.dismiss();
                    Toast.makeText(requireContext(), getString(R.string.failed_to_update_equipment) + e.getMessage(),
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
        builder.setTitle(getString(R.string.progress_update) + record.getChallenge().getName());

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(record.getCurrentProgress()));
        builder.setView(input);

        builder.setPositiveButton(R.string.update, (dialog, which) -> {
            try {
                int newProgress = Integer.parseInt(input.getText().toString());
                if (newProgress >= 0) {
                    record.updateProgress(newProgress);

                    firebaseManager.updateChallengeRecord(record)
                            .addOnSuccessListener(aVoid -> {
                                loadUserChallenges();
                                Toast.makeText(getContext(), R.string.the_progress_was_updated_successfully, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), getString(R.string.error_updating_progress) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {Toast.makeText(getContext(), R.string.please_enter_a_positive_number, Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {Toast.makeText(getContext(), R.string.please_enter_a_valid_number, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
}