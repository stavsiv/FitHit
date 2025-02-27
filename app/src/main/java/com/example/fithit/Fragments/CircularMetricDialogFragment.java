package com.example.fithit.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fithit.Managers.FirebaseManager;
import com.example.fithit.Models.DatabaseWorkouts;
import com.example.fithit.Models.Metric;
import com.example.fithit.Models.Workout;
import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;
import com.example.fithit.Views.CircularMetricsView;
import com.google.android.material.button.MaterialButton;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CircularMetricDialogFragment extends DialogFragment {
    private CircularMetricsView circularMetricsView;
    private SeekBar valueSeekBar;
    private TextView selectedMetricText;
    private FirebaseManager firebaseManager;
    private String selectedMetricType;
    private WorkoutRecord workoutRecord;
    private Workout currentWorkout;

    private Map<String, Double> currentMetrics = new HashMap<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseManager = FirebaseManager.getInstance();
        if (getArguments() != null) {
            int workoutId = getArguments().getInt("workoutId");
            currentWorkout = DatabaseWorkouts.getWorkoutById(workoutId);
            workoutRecord = new WorkoutRecord(currentWorkout, new Date());
            workoutRecord.setCompleted(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_circular_metric, container, false);
        initializeViews(view);
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        circularMetricsView = view.findViewById(R.id.circular_metrics_view);
        valueSeekBar = view.findViewById(R.id.value_seekbar);
        selectedMetricText = view.findViewById(R.id.selected_metric_text);

        MaterialButton saveButton = view.findViewById(R.id.save_button);
        MaterialButton skipButton = view.findViewById(R.id.skip_button);

        saveButton.setOnClickListener(v -> saveMetrics());
        skipButton.setOnClickListener(v -> navigateToMain());
    }

    private void setupListeners() {
        circularMetricsView.setOnMetricSelectedListener((metricType, value) -> {
            selectedMetricType = metricType;
            updateSelectedMetricText(metricType, valueSeekBar.getProgress());

            switch (metricType) {
                case Metric.WEIGHT:
                    valueSeekBar.setMax(200); // Max 200kg
                    break;
                case Metric.HEART_RATE:
                    valueSeekBar.setMax(220); // Max 220bpm
                    break;
                case Metric.STEPS:
                    valueSeekBar.setMax(8000); // Max 8000 steps
                    break;
            }

            if (currentMetrics.containsKey(metricType)) {
                valueSeekBar.setProgress(currentMetrics.get(metricType).intValue());
            } else {
                valueSeekBar.setProgress(0);
            }
        });

        valueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (selectedMetricType != null) {
                    circularMetricsView.setValue(progress);
                    updateSelectedMetricText(selectedMetricType, progress);
                    currentMetrics.put(selectedMetricType, (double) progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void saveMetrics() {
        if (!isAdded() || getContext() == null) {
            Log.w("MetricsDialog", "Fragment not attached or context is null. Skipping save.");
            return;
        }

        if (currentMetrics.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one metric", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MetricsDialog", "Saving metrics: " + currentMetrics.toString());

        for (Map.Entry<String, Double> entry : currentMetrics.entrySet()) {
            workoutRecord.addMetric(entry.getKey(), entry.getValue());
        }

        firebaseManager.addWorkoutRecord(workoutRecord)
                .addOnSuccessListener(aVoid -> {
                    if (isAdded() && getContext() != null) {
                        Log.d("MetricsDialog", "Metrics saved successfully");
                        Toast.makeText(getContext(), "Workout metrics saved successfully", Toast.LENGTH_SHORT).show();
                    }
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getContext() != null) {
                        Log.e("MetricsDialog", "Failed to save metrics", e);
                        Toast.makeText(getContext(), "Failed to save metrics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    navigateToMain();
                });
    }
    private void navigateToMain() {
        try {
            if (!isAdded() || getView() == null) {
                dismiss();
                return;
            }

            NavController navController = Navigation.findNavController(requireView());
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != R.id.fragmentMain) {
                navController.navigate(R.id.action_postWorkoutMetricsDialog_to_fragmentMain);
            } else {
                dismiss();
            }
        } catch (Exception e) {
            Log.e("Navigation", "Navigation error: " + e.getMessage());

            try {
                dismiss();
            } catch (Exception ex) {
                Log.e("Navigation", "Failed to dismiss dialog: " + ex.getMessage());
            }

            if (getActivity() != null) {
                try {
                    getActivity().recreate();
                } catch (Exception recreateEx) {
                    Log.e("Navigation", "Failed to recreate activity: " + recreateEx.getMessage());
                }
            }
        }
    }
    private void updateSelectedMetricText(String type, int value) {
        String unit = getUnitForMetric(type);
        StringBuilder metricsText = new StringBuilder();
        for (Map.Entry<String, Double> entry : currentMetrics.entrySet()) {
            metricsText.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue().intValue())
                    .append(getUnitForMetric(entry.getKey()))
                    .append("\n");
        }
        selectedMetricText.setText(metricsText.toString());
    }


    private String getUnitForMetric(String type) {
        switch (type) {
            case Metric.WEIGHT:
                return " kg";
            case Metric.HEART_RATE:
                return " bpm";
            case Metric.STEPS:
                return " steps";
            default:
                return "";
        }
    }
}