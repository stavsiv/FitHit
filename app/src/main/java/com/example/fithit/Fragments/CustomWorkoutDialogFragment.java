package com.example.fithit.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Managers.FirebaseManager;
import com.example.fithit.Models.Workout;
import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomWorkoutDialogFragment extends DialogFragment {
    private TextView durationText;
    private com.google.android.material.slider.Slider durationSlider;
    private TextView strengthPercentage, cardioPercentage, stretchingPercentage, balancePercentage;
    private TextView totalPercentage;
    private Button generateWorkoutButton;
    private Button strengthDecreaseBtn, strengthIncreaseBtn;
    private Button cardioDecreaseBtn, cardioIncreaseBtn;
    private Button stretchingDecreaseBtn, stretchingIncreaseBtn;
    private Button balanceDecreaseBtn, balanceIncreaseBtn;
    private Date selectedDate;

    private static final int MIN_DURATION = 15;
    private static final int MAX_DURATION = 60;
    private static final int DEFAULT_DURATION = 30;

    private static final int PERCENTAGE_STEP = 5;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_workout_dialog, container, false);


        try {
            initializeViews(view);
            if (getArguments() != null) {
                long dateInMillis = getArguments().getLong(getString(R.string.selectedDate));
                selectedDate = new Date(dateInMillis);
            } else {
                selectedDate = new Date();
            }

            setupDurationSlider();
            setupPercentageButtons();
            setupGenerateButton();
        } catch (Exception e) {
            //
        }

        return view;
    }

    private void initializeViews(View view) {

        durationText = view.findViewById(R.id.durationText);
        durationSlider = view.findViewById(R.id.durationSlider);

        strengthPercentage = view.findViewById(R.id.strengthPercentage);
        cardioPercentage = view.findViewById(R.id.cardioPercentage);
        stretchingPercentage = view.findViewById(R.id.stretchingPercentage);
        balancePercentage = view.findViewById(R.id.balancePercentage);

        totalPercentage = view.findViewById(R.id.totalPercentage);

        strengthDecreaseBtn = view.findViewById(R.id.strengthDecreaseBtn);

        strengthIncreaseBtn = view.findViewById(R.id.strengthIncreaseBtn);
        cardioDecreaseBtn = view.findViewById(R.id.cardioDecreaseBtn);
        cardioIncreaseBtn = view.findViewById(R.id.cardioIncreaseBtn);
        stretchingDecreaseBtn = view.findViewById(R.id.stretchingDecreaseBtn);
        stretchingIncreaseBtn = view.findViewById(R.id.stretchingIncreaseBtn);
        balanceDecreaseBtn = view.findViewById(R.id.balanceDecreaseBtn);
        balanceIncreaseBtn = view.findViewById(R.id.balanceIncreaseBtn);

        generateWorkoutButton = view.findViewById(R.id.generateWorkoutButton);
    }

    private void setupDurationSlider() {
        if (durationSlider != null) {
            durationSlider.setValueFrom(MIN_DURATION);
            durationSlider.setValueTo(MAX_DURATION);
            durationSlider.setValue(DEFAULT_DURATION);

            durationSlider.addOnChangeListener((slider, value, fromUser) -> durationText.setText(String.format(Locale.getDefault(), "%.0f", value)));
        }
    }

    private void setupPercentageButtons() {
        if (strengthIncreaseBtn != null && strengthDecreaseBtn != null) {
            // Strength buttons
            strengthIncreaseBtn.setOnClickListener(v -> adjustPercentage(strengthPercentage, PERCENTAGE_STEP));
            strengthDecreaseBtn.setOnClickListener(v -> adjustPercentage(strengthPercentage, -PERCENTAGE_STEP));
        } else {
            // Handle the case where the buttons are null
        }

        if (cardioIncreaseBtn != null && cardioDecreaseBtn != null) {
            // Cardio buttons
            cardioIncreaseBtn.setOnClickListener(v -> adjustPercentage(cardioPercentage, PERCENTAGE_STEP));
            cardioDecreaseBtn.setOnClickListener(v -> adjustPercentage(cardioPercentage, -PERCENTAGE_STEP));
        } else {
            //
        }

        if (stretchingIncreaseBtn != null && stretchingDecreaseBtn != null) {
            stretchingIncreaseBtn.setOnClickListener(v -> adjustPercentage(stretchingPercentage, PERCENTAGE_STEP));
            stretchingDecreaseBtn.setOnClickListener(v -> adjustPercentage(stretchingPercentage, -PERCENTAGE_STEP));
        } else {
            //
        }

        if (balanceIncreaseBtn != null && balanceDecreaseBtn != null) {
            balanceIncreaseBtn.setOnClickListener(v -> adjustPercentage(balancePercentage, PERCENTAGE_STEP));
            balanceDecreaseBtn.setOnClickListener(v -> adjustPercentage(balancePercentage, -PERCENTAGE_STEP));
        } else {
            //
        }
    }

    private void adjustPercentage(TextView percentageView, int change) {
        int currentValue = getCurrentPercentage(percentageView);
        int newValue = currentValue + change;

        if (newValue < 0 || newValue > 100) {
            return;
        }

        int totalWithoutCurrent = calculateTotalPercentage() - currentValue;
        if (totalWithoutCurrent + newValue > 100) {
            return;
        }

        percentageView.setText(newValue + "%");
        updateTotalPercentage();
    }

    private int getCurrentPercentage(TextView percentageView) {
        String text = percentageView.getText().toString();
        return Integer.parseInt(text.replace("%", ""));
    }

    private int calculateTotalPercentage() {
        return getCurrentPercentage(strengthPercentage) +
                getCurrentPercentage(cardioPercentage) +
                getCurrentPercentage(stretchingPercentage) +
                getCurrentPercentage(balancePercentage);
    }

    private void updateTotalPercentage() {
        int total = calculateTotalPercentage();
        totalPercentage.setText(getString(R.string.total) + " " + total + "%");
    }

    private void setupGenerateButton() {
        generateWorkoutButton.setOnClickListener(v -> validateAndGenerateWorkout());
    }

    private void validateAndGenerateWorkout() {
        int duration = (int) durationSlider.getValue();
        int total = calculateTotalPercentage();

        if (total != 100) {Toast.makeText(requireContext(), getString(R.string.total_percentage_must_be_100_currently) + total + "%)", Toast.LENGTH_SHORT).show();
            return;
        }

        Workout workout = Workout.generateCustomWorkout(
                requireContext(),
                duration,
                getCurrentPercentage(strengthPercentage),
                getCurrentPercentage(cardioPercentage),
                getCurrentPercentage(stretchingPercentage),
                getCurrentPercentage(balancePercentage),
                DifficultyLevel.BEGINNER,
                new ArrayList<EquipmentType>()
        );

        if (workout == null) {
            Toast.makeText(requireContext(), "Failed to generate workout", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date normalizedDate = calendar.getTime();

        WorkoutRecord workoutRecord = new WorkoutRecord(workout, normalizedDate);

        FirebaseManager.getInstance()
                .addWorkoutRecord(workoutRecord)
                .addOnSuccessListener(aVoid -> {   Log.d("CustomWorkout", "Record saved successfully");
                    Bundle args = new Bundle();
                    args.putSerializable("workout", workout);
                    args.putLong("date", normalizedDate.getTime());


                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_CustomWorkoutDialogFragment_to_fragmentWorkoutDetails, args);

                    dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(),R.string.failed_to_save_workout + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    public interface OnWorkoutGeneratedListener {
        void onWorkoutGenerated(Workout workout);
    }
}