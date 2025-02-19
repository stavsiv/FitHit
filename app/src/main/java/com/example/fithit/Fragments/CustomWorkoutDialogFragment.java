package com.example.fithit.Fragments;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Models.Workout;
import com.example.fithit.R;

import java.util.ArrayList;
import java.util.List;

public class CustomWorkoutDialogFragment extends DialogFragment {
    private EditText durationInput;
    private SeekBar strengthSeekBar, cardioSeekBar, stretchingSeekBar, balanceSeekBar;
    private TextView strengthPercent, cardioPercent, stretchingPercent, balancePercent;
    private TextView totalPercentage;
    private Button generateWorkoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_workout_dialog, container, false);
        initializeViews(view);
        setupSeekBarListeners();
        setupGenerateButton();
        return view;
    }

    private void initializeViews(View view) {
        durationInput = view.findViewById(R.id.duration_input);
        strengthSeekBar = view.findViewById(R.id.strength_seekbar);
        cardioSeekBar = view.findViewById(R.id.cardio_seekbar);
        stretchingSeekBar = view.findViewById(R.id.stretching_seekbar);
        balanceSeekBar = view.findViewById(R.id.balance_seekbar);
        strengthPercent = view.findViewById(R.id.strength_percent);
        cardioPercent = view.findViewById(R.id.cardio_percent);
        stretchingPercent = view.findViewById(R.id.stretching_percent);
        balancePercent = view.findViewById(R.id.balance_percent);
        totalPercentage = view.findViewById(R.id.total_percentage);
        generateWorkoutButton = view.findViewById(R.id.generate_workout_button);
    }

    private void setupSeekBarListeners() {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePercentages();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        strengthSeekBar.setOnSeekBarChangeListener(listener);
        cardioSeekBar.setOnSeekBarChangeListener(listener);
        stretchingSeekBar.setOnSeekBarChangeListener(listener);
        balanceSeekBar.setOnSeekBarChangeListener(listener);
    }

    private void updatePercentages() {
        int strength = strengthSeekBar.getProgress();
        int cardio = cardioSeekBar.getProgress();
        int stretching = stretchingSeekBar.getProgress();
        int balance = balanceSeekBar.getProgress();

        strengthPercent.setText(getString(R.string.percent_format, strength));
        cardioPercent.setText(getString(R.string.percent_format, cardio));
        stretchingPercent.setText(getString(R.string.percent_format, stretching));
        balancePercent.setText(getString(R.string.percent_format, balance));

        int total = strength + cardio + stretching + balance;
        totalPercentage.setText(getString(R.string.total_percent_format, total));
    }

    private void setupGenerateButton() {
        generateWorkoutButton.setOnClickListener(v -> validateAndGenerateWorkout());
    }

    private void validateAndGenerateWorkout() {
        // Validate duration
        String durationStr = durationInput.getText().toString();
        if (durationStr.isEmpty()) {
            showError(getString(R.string.error_enter_duration));
            return;
        }

        int duration = Integer.parseInt(durationStr);
        if (duration < 15 || duration > 60) {
            showError(getString(R.string.error_duration_range));
            return;
        }

        // Validate percentages
        int total = strengthSeekBar.getProgress() +
                cardioSeekBar.getProgress() +
                stretchingSeekBar.getProgress() +
                balanceSeekBar.getProgress();

        if (total != 100) {
            showError(getString(R.string.error_percentage_sum));
            return;
        }

        // Create workout using the static method from Workout class
        List<EquipmentType> availableEquipment = new ArrayList<>();
        Workout workout = Workout.generateCustomWorkout(
                duration,
                strengthSeekBar.getProgress(),
                cardioSeekBar.getProgress(),
                stretchingSeekBar.getProgress(),
                balanceSeekBar.getProgress(),
                DifficultyLevel.INTERMEDIATE,
                availableEquipment
        );

        // Notify parent fragment/activity
        if (getParentFragment() instanceof OnWorkoutGeneratedListener) {
            ((OnWorkoutGeneratedListener) getParentFragment()).onWorkoutGenerated(workout);
        }

        dismiss();
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Updated interface to use Workout instead of CustomWorkout
    public interface OnWorkoutGeneratedListener {
        void onWorkoutGenerated(Workout workout);
    }
}