package com.example.fithit.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.fithit.Enums.MetricType;
import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.example.fithit.Models.Metric;
import com.example.fithit.R;
import com.example.fithit.Views.CircularMetricsView;

import java.util.Date;

public class CircularMetricDialogFragment extends DialogFragment {
    private CircularMetricsView circularMetricsView;
    private SeekBar valueSeekBar;
    private TextView selectedMetricText;
    private MetricType selectedMetricType;
    private FirebaseManager firebaseManager;

    public interface MetricUpdateListener {
        void onMetricUpdated();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circular_metric, null);

        // Initialize Firebase
        firebaseManager = FirebaseManager.getInstance();

        // Initialize views
        circularMetricsView = view.findViewById(R.id.circular_metrics_view);
        valueSeekBar = view.findViewById(R.id.value_seekbar);
        selectedMetricText = view.findViewById(R.id.selected_metric_text);

        // Setup listeners
        setupListeners();

        builder.setView(view)
                .setTitle("Update Metrics")
                .setPositiveButton("Save", (dialog, which) -> saveMetric())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        return builder.create();
    }

    private void saveMetric() {
        if (selectedMetricType == null) {
            Toast.makeText(getContext(), "Please select a metric type", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        double value = valueSeekBar.getProgress();
        Metric newMetric = new Metric(userId, selectedMetricType, value, new Date(), "After workout");

        // Show loading indicator
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving metric...");
        progressDialog.show();

        firebaseManager.addMetric(userId, newMetric)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Metric saved successfully", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof MetricUpdateListener) {
                        ((MetricUpdateListener) getActivity()).onMetricUpdated();
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to save metric: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("MetricDialog", "Error saving metric", e);
                });
    }

    private void setupListeners() {
        // CircularMetricsView listener
        circularMetricsView.setOnMetricSelectedListener((metricType, value) -> {
            selectedMetricType = metricType;
            updateSelectedMetricText(metricType, valueSeekBar.getProgress());
        });

        // SeekBar listener
        valueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (selectedMetricType != null) {
                    circularMetricsView.setValue(progress);
                    updateSelectedMetricText(selectedMetricType, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateSelectedMetricText(MetricType type, int value) {
        String unit = getUnitForMetric(type);
        selectedMetricText.setText(String.format("%s: %d%s", type.toString(), value, unit));
    }

    private String getUnitForMetric(MetricType type) {
        switch (type) {
            case WEIGHT:
                return "kg";
            case HEART_RATE:
                return "bpm";
            case BODY_FAT:
            case MUSCLE_MASS:
                return "%";
            case ENDURANCE_TIME:
                return "min";
            default:
                return "";
        }
    }
}