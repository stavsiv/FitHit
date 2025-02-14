package com.example.fithit.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.fithit.FirebaseManagment.FirebaseManager;
import com.example.fithit.Models.Equipment;
import com.example.fithit.R;

public class AddEquipmentDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_equipment, null);

        EditText etEquipmentName = view.findViewById(R.id.et_equipment_name);
        EditText etDescription = view.findViewById(R.id.et_equipment_description);

        builder.setView(view)
                .setTitle("Add Equipment")
                .setPositiveButton("Add", (dialog, id) -> {
                    String name = etEquipmentName.getText().toString();
                    String description = etDescription.getText().toString();

                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter equipment name",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Equipment newEquipment = new Equipment(name, 0, description, "");
                    FirebaseManager.getInstance().addEquipment(newEquipment)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Equipment added successfully",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to add equipment: "
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }
}