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

import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.example.fithit.Models.Metric;
import com.example.fithit.R;
import com.example.fithit.Views.CircularMetricsView;

import java.util.Date;

public class CircularMetricDialogFragment extends DialogFragment {
    private static final String TAG = "MetricDialog";

    private CircularMetricsView circularMetricsView;
    private SeekBar valueSeekBar;
    private TextView selectedMetricText;
    private FirebaseManager firebaseManager;
    private String selectedMetricType;

    public interface MetricUpdateListener {
        void onMetricUpdated();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circular_metric, null);

        firebaseManager = FirebaseManager.getInstance();
        initializeViews(view);
        setupListeners();

        builder.setView(view)
                .setTitle("Update Metrics")
                .setPositiveButton("Save", (dialog, which) -> saveMetric())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        return builder.create();
    }

    private void initializeViews(View view) {
        circularMetricsView = view.findViewById(R.id.circular_metrics_view);
        valueSeekBar = view.findViewById(R.id.value_seekbar);
        selectedMetricText = view.findViewById(R.id.selected_metric_text);
    }

    private void saveMetric() {
        if (selectedMetricType == null) {
            Toast.makeText(getContext(), "Please select a metric type", Toast.LENGTH_SHORT).show();
            return;
        }

        Metric newMetric = new Metric();
        newMetric.setUserId(firebaseManager.getCurrentUserId());
        newMetric.addMetric(selectedMetricType, valueSeekBar.getProgress());

        ProgressDialog progressDialog = createAndShowProgressDialog();

        firebaseManager.addMetric(firebaseManager.getCurrentUserId(), newMetric)
                .addOnSuccessListener(aVoid -> handleSaveSuccess(progressDialog))
                .addOnFailureListener(e -> handleSaveFailure(e, progressDialog));
    }

    private ProgressDialog createAndShowProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving metric...");
        progressDialog.show();
        return progressDialog;
    }

    private void handleSaveSuccess(ProgressDialog progressDialog) {
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Metric saved successfully", Toast.LENGTH_SHORT).show();
        if (getActivity() instanceof MetricUpdateListener) {
            ((MetricUpdateListener) getActivity()).onMetricUpdated();
        }
        dismiss();
    }

    private void handleSaveFailure(Exception e, ProgressDialog progressDialog) {
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Failed to save metric: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error saving metric", e);
    }

    private void setupListeners() {
        setupCircularMetricsViewListener();
        setupSeekBarListener();
    }

    private void setupCircularMetricsViewListener() {
        circularMetricsView.setOnMetricSelectedListener((metricType, value) -> {
            selectedMetricType = String.valueOf(metricType);
            updateSelectedMetricText(String.valueOf(metricType), valueSeekBar.getProgress());
        });
    }

    private void setupSeekBarListener() {
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

    private void updateSelectedMetricText(String type, int value) {
        String unit = getUnitForMetric(type);
        selectedMetricText.setText(String.format("%s: %d%s", type, value, unit));
    }
    //צריך להשים הגבלה על משקל , קלט וכו

    private String getUnitForMetric(String type) {
        switch (type) {
            case Metric.WEIGHT:
                return "kg";
            case Metric.HEART_RATE:
                return "bpm";
            case Metric.CALORIES:
                return "cal";
            case Metric.STEPS:
                return "steps";
            default:
                return "";
        }
    }
}