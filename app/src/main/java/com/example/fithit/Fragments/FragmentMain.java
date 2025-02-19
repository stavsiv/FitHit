package com.example.fithit.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.EditText;
import android.widget.SeekBar;

import androidx.navigation.Navigation;

import androidx.fragment.app.Fragment;

import com.example.fithit.Models.Workout;
import com.example.fithit.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentMain extends Fragment {
    // UI Components
    private MaterialCardView btnToPersonalArea;
    private TextView pointsText;
    private TextView goalsProgressText;
    private ProgressBar levelProgressBar;
    private MaterialCardView upcomingWorkoutAlert;
    private CalendarView calendarView;
    private MaterialButton btnAddWorkout;
    private MaterialButton btnCancelWorkout;

    // Selected date for workout management
    private Date selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initializeViews(view);
        setupClickListeners();
        loadUserData();
        return view;
    }

    private void initializeViews(View view) {
        // Initialize all UI components
        btnToPersonalArea = view.findViewById(R.id.btn_to_personal_area);
        pointsText = view.findViewById(R.id.points_text);
        goalsProgressText = view.findViewById(R.id.goals_progress_text);
        levelProgressBar = view.findViewById(R.id.level_progress_bar);
        upcomingWorkoutAlert = view.findViewById(R.id.upcoming_workout_alert);
        calendarView = view.findViewById(R.id.calendar_view);
        btnAddWorkout = view.findViewById(R.id.btn_add_workout);
        btnCancelWorkout = view.findViewById(R.id.btn_cancel_workout);

        // Set initial states
        btnAddWorkout.setEnabled(false);
        btnCancelWorkout.setEnabled(false);
    }

    private void setupClickListeners() {
        // Personal Area navigation
        btnToPersonalArea.setOnClickListener(v -> navigateToPersonalArea());

        // Calendar date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();

            updateWorkoutButtons();
        });

        // Workout management buttons
        btnAddWorkout.setOnClickListener(v -> showAddWorkoutDialog());  // הוספנו את זה
        btnCancelWorkout.setOnClickListener(v -> showCancelWorkoutDialog());
    }

    @SuppressLint("DefaultLocale")
    private void loadUserData() {
        // TODO: Replace with actual data from your database/repository
        int currentPoints = getCurrentUserPoints();
        int maxPoints = getPointsForNextLevel();
        int weeklyWorkouts = getWeeklyWorkoutsCount();
        int weeklyGoal = getWeeklyWorkoutGoal();

        // Update UI with user data
        pointsText.setText(String.format("Current Hearts: %d/%d", currentPoints, maxPoints));
        goalsProgressText.setText(String.format("Weekly Progress: %d/%d workouts", weeklyWorkouts, weeklyGoal));
        levelProgressBar.setProgress((currentPoints * 100) / maxPoints);

        // Check and display upcoming workout alert if needed
        checkUpcomingWorkout();
    }

    private void updateWorkoutButtons() {
        if (selectedDate == null) return;

        boolean hasWorkout = checkIfDateHasWorkout(selectedDate);
        boolean isPastDate = selectedDate.before(new Date());

        btnAddWorkout.setEnabled(!hasWorkout && !isPastDate);
        btnCancelWorkout.setEnabled(hasWorkout && !isPastDate);
    }

    private void checkUpcomingWorkout() {
        // TODO: Check if there's an upcoming workout in the next week
        Workout nextWorkout = getNextScheduledWorkout();
        if (nextWorkout != null) {
            showUpcomingWorkoutAlert(nextWorkout);
        }
    }

    private void showUpcomingWorkoutAlert(Workout workout) {
        upcomingWorkoutAlert.setVisibility(View.VISIBLE);
        TextView alertText = upcomingWorkoutAlert.findViewById(R.id.alert_text);
        String alertMessage = String.format("Upcoming workout: %s", workout.getName());
        alertText.setText(alertMessage);
    }

    private void navigateToPersonalArea() {
        try {
            Navigation.findNavController(getView()).navigate(R.id.action_fragmentMain_to_fragmentPersonalArea);
        } catch (Exception e) {
            Log.e("Navigation", "Failed to navigate to personal area fragment", e);
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context,
                        "Failed to navigate: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAddWorkoutDialog() {
        Context context = getContext();
        if (context != null) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Add Workout")
                    .setMessage("Choose workout type:")
                    .setPositiveButton("Choose from existing workouts", (dialog, which) -> {
                        Bundle args = new Bundle();
                        args.putLong("selectedDate", selectedDate.getTime());
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_fragmentMain_to_fragmentWorkouts, args);
                    })
                    .setNegativeButton("Create custom workout", (dialog, which) -> {
                        showCustomWorkoutDialog();
                    })
                    .show();
        }
    }

    private void showCustomWorkoutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom_workout, null);

        // Initialize views
        EditText durationInput = dialogView.findViewById(R.id.duration_input);
        SeekBar strengthSeekBar = dialogView.findViewById(R.id.strength_seekbar);
        SeekBar cardioSeekBar = dialogView.findViewById(R.id.cardio_seekbar);
        SeekBar stretchingSeekBar = dialogView.findViewById(R.id.stretching_seekbar);
        SeekBar balanceSeekBar = dialogView.findViewById(R.id.balance_seekbar);

        TextView strengthPercent = dialogView.findViewById(R.id.strength_percent);
        TextView cardioPercent = dialogView.findViewById(R.id.cardio_percent);
        TextView stretchingPercent = dialogView.findViewById(R.id.stretching_percent);
        TextView balancePercent = dialogView.findViewById(R.id.balance_percent);
        TextView totalPercentage = dialogView.findViewById(R.id.total_percentage);

        // Set up seekbar listeners
        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Calculate total percentage
                int total = strengthSeekBar.getProgress() +
                        cardioSeekBar.getProgress() +
                        stretchingSeekBar.getProgress() +
                        balanceSeekBar.getProgress();

                // Update percentage texts
                strengthPercent.setText(strengthSeekBar.getProgress() + "%");
                cardioPercent.setText(cardioSeekBar.getProgress() + "%");
                stretchingPercent.setText(stretchingSeekBar.getProgress() + "%");
                balancePercent.setText(balanceSeekBar.getProgress() + "%");
                totalPercentage.setText("Total: " + total + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        // Set listeners
        strengthSeekBar.setOnSeekBarChangeListener(seekBarListener);
        cardioSeekBar.setOnSeekBarChangeListener(seekBarListener);
        stretchingSeekBar.setOnSeekBarChangeListener(seekBarListener);
        balanceSeekBar.setOnSeekBarChangeListener(seekBarListener);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create Custom Workout")
                .setView(dialogView)
                .setPositiveButton("Generate Workout", (dialog, which) -> {
                    // Validate duration
                    String durationStr = durationInput.getText().toString();
                    if (durationStr.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter workout duration", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int duration = Integer.parseInt(durationStr);
                    if (duration < 15 || duration > 60) {
                        Toast.makeText(getContext(), "Duration must be between 15 and 60 minutes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate percentages
                    int total = strengthSeekBar.getProgress() +
                            cardioSeekBar.getProgress() +
                            stretchingSeekBar.getProgress() +
                            balanceSeekBar.getProgress();

                    if (total != 100) {
                        Toast.makeText(getContext(), "Exercise percentages must sum to exactly 100%", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // All validations passed, create workout
                    createCustomWorkout(duration,
                            strengthSeekBar.getProgress(),
                            cardioSeekBar.getProgress(),
                            stretchingSeekBar.getProgress(),
                            balanceSeekBar.getProgress());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void createCustomWorkout(int duration, int strengthPercentage, int cardioPercentage,
                                     int stretchingPercentage, int balancePercentage) {
        // TODO: Implement custom workout creation
        // This will be implemented later
        Toast.makeText(getContext(), "Creating custom workout...", Toast.LENGTH_SHORT).show();
    }
    private void showCancelWorkoutDialog() {
        Context context = getContext();
        if (context != null) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Cancel Workout")
                    .setMessage("Are you sure you want to cancel this workout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        cancelWorkout();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void cancelWorkout() {
        if (selectedDate != null) {
            // TODO: Implement workout cancellation logic
            Toast.makeText(getContext(), "Workout cancelled", Toast.LENGTH_SHORT).show();
            updateWorkoutButtons();
            loadUserData();
        }
    }

    // מתודות זמניות לטובת הדוגמה - יוחלפו בהמשך במימוש אמיתי
    private int getCurrentUserPoints() { return 50; }
    private int getPointsForNextLevel() { return 100; }
    private int getWeeklyWorkoutsCount() { return 3; }
    private int getWeeklyWorkoutGoal() { return 7; }
    private boolean checkIfDateHasWorkout(Date date) {
        // TODO: Implement actual check
        return false;
    }
    private Workout getNextScheduledWorkout() {
        // TODO: Implement actual next workout fetch
        return null;
    }

}
