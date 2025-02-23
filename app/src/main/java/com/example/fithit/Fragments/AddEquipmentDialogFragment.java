package com.example.fithit.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.example.fithit.Models.Equipment;
import java.util.Arrays;

public class AddEquipmentDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        final String[] equipmentNames = Arrays.stream(EquipmentType.values())
                .map(EquipmentType::getDisplayName)
                .toArray(String[]::new);

        builder.setTitle("Select Equipment")
                .setItems(equipmentNames, (dialog, which) -> {
                    EquipmentType selectedType = EquipmentType.values()[which];
                    Equipment newEquipment = new Equipment(selectedType);

                    String userId = FirebaseManager.getInstance().getCurrentUserId();
                    if (userId != null) {
                        FirebaseManager.getInstance().addOrUpdateUserEquipment(userId, newEquipment)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                                        selectedType.getDisplayName() + " added successfully",
                                        Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(),
                                        "Failed to add " + selectedType.getDisplayName() +
                                                ": " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(getContext(),
                                "No user logged in",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}