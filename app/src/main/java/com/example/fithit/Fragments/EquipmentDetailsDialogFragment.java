package com.example.fithit.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.fithit.Models.Equipment;

public class EquipmentDetailsDialogFragment extends DialogFragment {
    private static final String ARG_EQUIPMENT = "equipment";

    public static EquipmentDetailsDialogFragment newInstance(Equipment equipment) {
        EquipmentDetailsDialogFragment fragment = new EquipmentDetailsDialogFragment();
        Bundle args = new Bundle();
        // Add equipment details to args
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        // Build your dialog here
        return builder.create();
    }
}