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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Adapters.EquipmentAdapter;
import com.example.fithit.R;
import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.User;
import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.github.mikephil.charting.charts.LineChart;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentPersonalArea extends Fragment implements EquipmentAdapter.OnEquipmentClickListener {

    private FirebaseManager firebaseManager;
    private View rootView;
    private RecyclerView equipmentRecyclerView;
    private RecyclerView metricsRecyclerView;
    private LineChart progressChart;
    private EquipmentAdapter equipmentAdapter;
    // private MetricsAdapter metricsAdapter;
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
    }

    private void initializeViews() {
        equipmentRecyclerView = rootView.findViewById(R.id.rv_equipment);
        metricsRecyclerView = rootView.findViewById(R.id.rv_metrics);
        progressChart = rootView.findViewById(R.id.chart_progress);
        tvUserName = rootView.findViewById(R.id.tv_user_name);
        tvUserLevel = rootView.findViewById(R.id.tv_user_level);
        progressLevel = rootView.findViewById(R.id.progress_level);

        FloatingActionButton fabAddEquipment = rootView.findViewById(R.id.btn_add_equipment);
        fabAddEquipment.setOnClickListener(v -> showAddEquipmentDialog());

        FloatingActionButton fabAddMetric = rootView.findViewById(R.id.fab_add_metric);
        //  fabAddMetric.setOnClickListener(v -> showAddMetricDialog());
    }

    private void setupRecyclerViews() {
        // Setup Equipment RecyclerView
        equipmentAdapter = new EquipmentAdapter(new ArrayList<>(), this);
        equipmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        equipmentRecyclerView.setAdapter(equipmentAdapter);
        equipmentRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

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
        firebaseManager.getUserData(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                User user = snapshot.getValue(User.class);
                if (user != null) {
                    //updateUserUI(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(),
                        "Failed to load user data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        firebaseManager.getUserEquipment(userId)
                .addOnSuccessListener(equipmentList -> {
                    if (!isAdded()) return;
                    equipmentAdapter.updateEquipment(equipmentList);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(),
                            "Failed to load equipment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

//        // Load user metrics
//        firebaseManager.getLatestMetrics(userId, 7, new FirebaseManager.OnMetricsLoadedListener() {
//            @Override
//            public void onMetricsLoaded(List<Metric> metrics) {
//                if (isAdded()) {
//                   // updateMetricsUI(metrics);
//                }
//            }

//            @Override
//            public void onError(String error) {
//                if (isAdded()) {
//                    Toast.makeText(requireContext(),
//                            "Failed to load metrics: " + error,
//                            Toast.LENGTH_SHORT).show();
//                }
//            }


    //    private void updateUserUI(User user) {
//        if (!isAdded()) return;
//
//        tvUserName.setText(user.getUserName());
//        tvUserLevel.setText(getString(R.string.level_format, user.getLevel()));
//
//        if (progressLevel instanceof ProgressBar) {
//            int progress = (user.getTotalWorkouts() % 10) * 10; // 10 workouts per level
//            ((ProgressBar) progressLevel).setProgress(progress);
//        }
//    }
//
//    private void updateMetricsUI(List<Metric> metrics) {
//        if (!isAdded() || metrics == null || metrics.isEmpty()) return;
//
//        // Update chart data here if needed
//        progressChart.clear();
//        // Add your chart update logic
//    }
    @Override
    public void onEquipmentClick(Equipment equipment) {
        if (!isAdded() || equipment == null) return;
        showEquipmentDetailsDialog(equipment);
    }


    // @Override
//    public void onEquipmentRemove(Equipment equipment) {
//        if (!isAdded() || equipment == null) return;
//
//        new AlertDialog.Builder(requireContext())
//                .setTitle(R.string.remove_equipment_title)
//                .setMessage(getString(R.string.remove_equipment_message, equipment.getDisplayName()))
//                .setPositiveButton(R.string.yes, (dialog, which) -> {
//                    String userId = firebaseManager.getCurrentUserId();
//                    if (userId != null) {
//                        firebaseManager.removeEquipmentFromUser(userId, equipment.getId())
//                                .addOnSuccessListener(aVoid -> {
//                                    if (!isAdded()) return;
//                                    Toast.makeText(requireContext(),
//                                            R.string.equipment_removed_success,
//                                            Toast.LENGTH_SHORT).show();
//                                    loadUserData();  // Refresh the list
//                                })
//                                .addOnFailureListener(e -> {
//                                    if (!isAdded()) return;
//                                    Toast.makeText(requireContext(),
//                                            getString(R.string.equipment_removed_error, e.getMessage()),
//                                            Toast.LENGTH_SHORT).show();
//                                });
//                    }
//                })
//                .setNegativeButton(R.string.no, null)
//                .show();
//    }

    private void showAddEquipmentDialog() {
        if (!isAdded()) return;
        AddEquipmentDialogFragment dialog = new AddEquipmentDialogFragment();
        dialog.show(getChildFragmentManager(), "AddEquipment");
    }

//    private void showAddMetricDialog() {
//        CircularMetricDialogFragment dialog = new CircularMetricDialogFragment();
//        dialog.show(getChildFragmentManager(), "AddMetric");
//    }

    private void showEquipmentDetailsDialog(Equipment equipment) {
        EquipmentDetailsDialogFragment dialog = EquipmentDetailsDialogFragment.newInstance(equipment);
        dialog.show(getChildFragmentManager(), "EquipmentDetails");
    }
}