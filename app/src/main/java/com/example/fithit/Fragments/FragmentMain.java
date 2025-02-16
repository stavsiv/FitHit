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
        //btnAddWorkout.setOnClickListener(v -> showAddWorkoutDialog());
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

        // Enable/disable buttons based on whether the selected date has a workout
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
        TextView alertText = upcomingWorkoutAlert.findViewById(android.R.id.text1);
        String alertMessage = String.format("Upcoming workout: %s on %s",
                workout.getType(),
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(workout.getDate()));
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

//    private void showAddWorkoutDialog() {
//        // TODO: Implement add workout dialog
//        WorkoutDialogFragment dialog = WorkoutDialogFragment.newInstance(selectedDate);
//        dialog.show(getChildFragmentManager(), "AddWorkout");
//    }

    private void showCancelWorkoutDialog() {
        Context context = getContext();
        if (context != null) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Cancel Workout")
                    .setMessage("Are you sure you want to cancel this workout?")
                    .setPositiveButton("Yes", (dialog, which) -> cancelWorkout())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            Log.e("Dialog", "Context is null, cannot show cancel workout dialog");
        }
    }

    private void cancelWorkout() {
        // TODO: Implement workout cancellation logic
        if (selectedDate != null) {
            // Delete workout from database
            // Update UI
            updateWorkoutButtons();
            loadUserData();
        }
    }

    // TODO: Implement these methods to get actual data from your backend/database
    private int getCurrentUserPoints() { return 0; }
    private int getPointsForNextLevel() { return 100; }
    private int getWeeklyWorkoutsCount() { return 3; }
    private int getWeeklyWorkoutGoal() { return 7; }
    private boolean checkIfDateHasWorkout(Date date) { return false; }
    private Workout getNextScheduledWorkout() { return null; }

    // Workout data class (you might want to move this to a separate file)

}
