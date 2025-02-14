package com.example.fithit.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.fithit.Enums.MetricType;
import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.example.fithit.Models.Metric;
import com.example.fithit.R;

import java.util.Date;

public class AddMetricDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_metric, null);

        Spinner spMetricType = view.findViewById(R.id.sp_metric_type);
        EditText etMetricValue = view.findViewById(R.id.et_metric_value);
        EditText etMetricNotes = view.findViewById(R.id.et_metric_notes);

        // Setup metric type spinner
        ArrayAdapter<MetricType> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, MetricType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetricType.setAdapter(adapter);

        builder.setView(view)
                .setTitle("Add Metric")
                .setPositiveButton("Add", (dialog, id) -> {
                    try {
                        double value = Double.parseDouble(etMetricValue.getText().toString());
                        MetricType type = (MetricType) spMetricType.getSelectedItem();
                        String notes = etMetricNotes.getText().toString();

                        String userId = FirebaseManager.getInstance().getCurrentUserId();
                        if (userId == null) {
                            Toast.makeText(getContext(), "User not logged in",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Metric newMetric = new Metric(0, Integer.parseInt(userId), type,
                                value, new Date(), notes);

                        FirebaseManager.getInstance().addMetric(userId, newMetric)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Metric added successfully",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to add metric: "
                                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Please enter a valid number",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }
}