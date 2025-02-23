package com.example.fithit.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Adapters.EquipmentAdapter;
import com.example.fithit.R;
import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.User;
import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.github.mikephil.charting.charts.LineChart;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class FragmentPersonalArea extends Fragment {

    private FirebaseManager firebaseManager;
    private View rootView;
    private RecyclerView equipmentRecyclerView;
    private LineChart progressChart;
    private EquipmentAdapter equipmentAdapter;
    private TextView tvUserName;
    private TextView tvUserLevel;
    private View progressLevel;

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

        // Add refresh listener for equipment updates
        getChildFragmentManager().setFragmentResultListener(
                "equipment_updated",
                getViewLifecycleOwner(),
                (requestKey, result) -> loadUserData()
        );
    }

    private void initializeViews() {
        equipmentRecyclerView = rootView.findViewById(R.id.rv_equipment);
        progressChart = rootView.findViewById(R.id.chart_progress);
        tvUserName = rootView.findViewById(R.id.tv_user_name);
        tvUserLevel = rootView.findViewById(R.id.tv_user_level);
        progressLevel = rootView.findViewById(R.id.progress_level);

        MaterialButton btnAddEquipment = rootView.findViewById(R.id.btn_add_equipment);
        btnAddEquipment.setOnClickListener(v -> showAddEquipmentDialog());
    }

    private void setupRecyclerViews() {
        // Setup Equipment RecyclerView
        equipmentAdapter = new EquipmentAdapter(new ArrayList<>(), new EquipmentAdapter.OnEquipmentSelectionChangedListener() {
            @Override
            public void onEquipmentSelectionChanged(Equipment equipment, boolean isSelected) {
                updateEquipmentInFirebase(equipment, isSelected);
            }
        });

        equipmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        equipmentRecyclerView.setAdapter(equipmentAdapter);
    }

    private void setupChart() {
        progressChart.getDescription().setEnabled(false);
        progressChart.setTouchEnabled(true);
        progressChart.setDragEnabled(true);
        progressChart.setScaleEnabled(true);
        progressChart.setPinchZoom(true);
    }

    private void loadUserData() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load user profile
        firebaseManager.getCurrentUserData()
                .addOnSuccessListener(user -> {
                    if (!isAdded()) return;
                    updateUserProfileUI(user);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "Failed to load user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });

        // Load user equipment
        firebaseManager.getUserEquipment(userId)
                .addOnSuccessListener(equipmentList -> {
                    if (!isAdded()) return;
                    equipmentAdapter.updateEquipmentList(equipmentList);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "Failed to load equipment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserProfileUI(User user) {
        if (user == null || !isAdded()) return;

        // Update user name
        tvUserName.setText(user.getUserName() != null ? user.getUserName() : "User");

        // Update user level (assuming User model has appropriate methods)
        if (user.getLevel() > 0) {
            tvUserLevel.setText(getString(R.string.level_format, user.getLevel()));
        }
    }

    private void updateEquipmentInFirebase(Equipment equipment, boolean isSelected) {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        if (isSelected) {
            // Add equipment to user
            firebaseManager.addEquipment(equipment)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return firebaseManager.addEquipmentToUser(userId, task.getResult().toString());
                    })
                    .addOnSuccessListener(aVoid -> {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(),
                                "Equipment added successfully",
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(),
                                "Failed to add equipment: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Remove equipment from user (implement this method in FirebaseManager)
            // firebaseManager.removeEquipmentFromUser(userId, equipment.getId())
            //     .addOnSuccessListener(...)
            //     .addOnFailureListener(...);
        }
    }

    private void showAddEquipmentDialog() {
        if (!isAdded()) return;

        EquipmentSelectionDialogFragment dialog =
                EquipmentSelectionDialogFragment.newInstance(selectedEquipment -> {
                    // Optional: Handle any additional logic after equipment selection
                    loadUserData();
                });

        dialog.show(getChildFragmentManager(), "EquipmentSelection");
    }
}