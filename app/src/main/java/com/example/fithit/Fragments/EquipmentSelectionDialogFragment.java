package com.example.fithit.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Adapters.EquipmentAdapter;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.example.fithit.Models.Equipment;
import com.example.fithit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EquipmentSelectionDialogFragment extends DialogFragment
        implements EquipmentAdapter.OnEquipmentSelectionChangedListener {

    private EquipmentAdapter adapter;
    private List<Equipment> equipmentList;
    private OnEquipmentUpdateListener listener;

    public interface OnEquipmentUpdateListener {
        void onEquipmentUpdated(List<Equipment> selectedEquipment);
    }

    public static EquipmentSelectionDialogFragment newInstance(OnEquipmentUpdateListener listener) {
        EquipmentSelectionDialogFragment fragment = new EquipmentSelectionDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the list of all equipment types
        equipmentList = new ArrayList<>();
        for (EquipmentType type : EquipmentType.values()) {
            equipmentList.add(new Equipment(type));
        }

        // Set up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_equipment, null);

        // Set up RecyclerView
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_equipment_selection);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create and set the adapter
        adapter = new EquipmentAdapter(equipmentList, this);
        recyclerView.setAdapter(adapter);

        // Configure dialog
        builder.setTitle("Select Your Equipment")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get selected equipment
                    List<Equipment> selectedEquipment = equipmentList.stream()
                            .filter(Equipment::isSelected)
                            .collect(Collectors.toList());

                    // Update Firebase with selected equipment
                    updateEquipmentInFirebase(selectedEquipment);
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private void updateEquipmentInFirebase(List<Equipment> selectedEquipment) {
        String userId = FirebaseManager.getInstance().getCurrentUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove existing equipment and add new selections
        for (Equipment equipment : selectedEquipment) {
            FirebaseManager.getInstance().addEquipment(equipment)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        String equipmentId = task.getResult().toString();
                        return FirebaseManager.getInstance().addEquipmentToUser(userId, equipmentId);
                    })
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onEquipmentUpdated(selectedEquipment);
                        }
                        Toast.makeText(getContext(), "Equipment updated successfully",
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),
                                "Failed to update equipment: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onEquipmentSelectionChanged(Equipment equipment, boolean isSelected) {
        // Optional: Add any additional logic when equipment selection changes
    }
}