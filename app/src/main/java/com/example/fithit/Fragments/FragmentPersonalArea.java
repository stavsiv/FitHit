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
import com.example.fithit.Adapters.MetricsAdapter;
import com.example.fithit.R;
import com.example.fithit.Models.Equipment;
import com.example.fithit.Models.Metric;
import com.example.fithit.Models.User;
import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentPersonalArea extends Fragment implements EquipmentAdapter.OnEquipmentClickListener {

    private FirebaseManager firebaseManager;
    private View rootView;
    private RecyclerView equipmentRecyclerView;
    private RecyclerView metricsRecyclerView;
    private LineChart progressChart;
    private EquipmentAdapter equipmentAdapter;
    private MetricsAdapter metricsAdapter;
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

        FloatingActionButton fabAddMetric = rootView.findViewById(R.id.fab_add_metric);
        fabAddMetric.setOnClickListener(v -> showAddMetricDialog());

        rootView.findViewById(R.id.btn_add_equipment).setOnClickListener(v -> showAddEquipmentDialog());
    }

    private void setupRecyclerViews() {
        // Setup Equipment RecyclerView
        equipmentAdapter = new EquipmentAdapter(new ArrayList<>(), this);
        equipmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        equipmentRecyclerView.setAdapter(equipmentAdapter);

        // Setup Metrics RecyclerView
        metricsAdapter = new MetricsAdapter(new ArrayList<>());
        metricsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        metricsRecyclerView.setAdapter(metricsAdapter);
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
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    updateUserUI(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Failed to load user data: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load user equipment
        // Load user equipment
        firebaseManager.getUserEquipment(userId)
                .addOnSuccessListener(dataSnapshot -> {
                    List<Equipment> equipmentList = new ArrayList<>();
                    // Iterating through the DataSnapshot if needed
                    for (Equipment equipment : dataSnapshot) { // assuming dataSnapshot is already a List<Equipment>
                        equipmentList.add(equipment);
                    }

                    if (equipmentAdapter != null && isAdded()) {
                        equipmentAdapter.updateEquipment(equipmentList);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(),
                                "Failed to load equipment: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Load user metrics
        firebaseManager.getLatestMetrics(userId, 7, new FirebaseManager.OnMetricsLoadedListener() {
            @Override
            public void onMetricsLoaded(List<Metric> metrics) {
                if (isAdded()) {
                    updateMetricsUI(metrics);
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Failed to load metrics: " + error,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserUI(User user) {
        if (!isAdded()) return;

        tvUserName.setText(user.getUserName());
        tvUserLevel.setText("Level " + user.getLevel());

        if (progressLevel instanceof android.widget.ProgressBar) {
            int progress = (user.getTotalWorkouts() % 10) * 10; // 10 workouts per level
            ((android.widget.ProgressBar) progressLevel).setProgress(progress);
        }
    }

    private void updateMetricsUI(List<Metric> metrics) {
        if (!isAdded()) return;

        if (metricsAdapter != null) {
            metricsAdapter.updateMetrics(metrics);
        }

        if (!metrics.isEmpty() && progressChart != null) {
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < metrics.size(); i++) {
                entries.add(new Entry(i, (float) metrics.get(i).getValue()));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Progress");
            dataSet.setDrawFilled(true);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData lineData = new LineData(dataSet);
            progressChart.setData(lineData);
            progressChart.invalidate();
        }
    }

    @Override
    public void onEquipmentClick(Equipment equipment) {
        if (!isAdded()) return;
        showEquipmentDetailsDialog(equipment);
    }

    private void showAddEquipmentDialog() {
        AddEquipmentDialogFragment dialog = new AddEquipmentDialogFragment();
        dialog.show(getChildFragmentManager(), "AddEquipment");
    }

    private void showAddMetricDialog() {
        CircularMetricDialogFragment dialog = new CircularMetricDialogFragment();
        dialog.show(getChildFragmentManager(), "AddMetric");
    }

    private void showEquipmentDetailsDialog(Equipment equipment) {
        EquipmentDetailsDialogFragment dialog = EquipmentDetailsDialogFragment.newInstance(equipment);
        dialog.show(getChildFragmentManager(), "EquipmentDetails");
    }
}