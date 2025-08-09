package com.example.fithit.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Adapters.UserWorkoutsAdapter;
import com.example.fithit.Enums.DifficultyLevel;
import com.example.fithit.Managers.FirebaseManager;
import com.example.fithit.Models.Workout;
import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentMain extends Fragment {
    private MaterialCardView btnToPersonalArea;
    private MaterialCardView upcomingWorkoutAlert;
    private CalendarView calendarView;
    private MaterialButton btnAddWorkout;
    private MaterialButton btnCancelWorkout;
    private Date selectedDate;

    private RecyclerView selectedDateWorkouts;
    private TextView noWorkoutsText;
    private UserWorkoutsAdapter workoutsAdapter;

    private ValueEventListener selectedDateWorkoutsListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initializeViews(view);
        setupClickListeners();
        loadUserData();
        selectedDate = new Date();
        calendarView.setDate(selectedDate.getTime());
        loadWorkoutsForSelectedDate();

        return view;
    }

    @SuppressLint("ResourceType")
    private void initializeViews(View view) {
        btnToPersonalArea = view.findViewById(R.id.btn_to_personal_area);
        upcomingWorkoutAlert = view.findViewById(R.id.upcoming_workout_alert);
        calendarView = view.findViewById(R.id.calendar_view);
        btnAddWorkout = view.findViewById(R.id.btn_add_workout);
        btnCancelWorkout = view.findViewById(R.id.btn_cancel_workout);
        selectedDateWorkouts = view.findViewById(R.id.selected_date_workouts);
        noWorkoutsText = view.findViewById(R.id.no_workouts_text);

        upcomingWorkoutAlert.setVisibility(View.GONE);

        selectedDateWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutsAdapter = new UserWorkoutsAdapter();

        workoutsAdapter.setOnWorkoutClickListener(this::showWorkoutDetailsDialog);

        selectedDateWorkouts.setAdapter(workoutsAdapter);
    }
    private void showWorkoutDetailsDialog(WorkoutRecord workout) {
        Context context = getContext();
        if (context != null && workout != null && workout.getWorkout() != null) {
            Workout workoutObj = workout.getWorkout();
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                    .setTitle(workoutObj.getName())
                    .setMessage(workoutObj.getDescription() + "\n\n" +
                            getString(R.string.workout_duration) + " " + workoutObj.getEstimatedDuration() + " " + getString(R.string.minutes) + "\n" +
                            getString(R.string.workout_difficulty) + " " + getDifficultyString(workoutObj.getDifficultyLevel()) + "\n" +
                            getString(R.string.workout_status) + " " + (workout.isCompleted() ? getString(R.string.completed_v) : getString(R.string.not_completed)))
                    .setPositiveButton(R.string.close, null);

            if (!workout.isCompleted()) {builder.setNeutralButton(R.string.mark_as_completed, (dialog, which) -> {
                    markWorkoutAsCompleted(workout);
                });
            }

            builder.show();
        }
    }
    private String getDifficultyString(DifficultyLevel level) {
        switch (level) {
            case BEGINNER:
                return getString(R.string.beginner);
            case INTERMEDIATE:
                return getString(R.string.intermediate);
            case EXPERT:
                return getString(R.string.expert);
            default:
                return level.toString();
        }
    }
    private void markWorkoutAsCompleted(WorkoutRecord workout) {
        if (workout == null || workout.getWorkout() == null) {
            Context context = getContext();
            if (context != null) {Toast.makeText(context, R.string.error_workout_data_is_missing, Toast.LENGTH_SHORT).show();}
            return;
        }

        FirebaseManager.getInstance().getWorkoutsByDate(new Date(workout.getDate()), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean workoutFound = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        WorkoutRecord record = snapshot.getValue(WorkoutRecord.class);
                        if (record != null && record.getWorkout() != null &&
                                record.getWorkout().getName().equals(workout.getWorkout().getName())) {

                            final Workout workoutToNavigate = workout.getWorkout();

                            snapshot.getRef().child("completed").setValue(true)
                                    .addOnSuccessListener(aVoid -> {
                                        if (isAdded() && getContext() != null) {Toast.makeText(getContext(), R.string.workout_marked_as_completed, Toast.LENGTH_SHORT).show();
                                            loadWorkoutsForSelectedDate();
                                            navigateToCircularMetricDialog(workoutToNavigate);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (isAdded() && getContext() != null) { Toast.makeText(getContext(), getString(R.string.failed_to_update_workout) + e.getMessage(), Toast.LENGTH_SHORT).show();}
                                    });

                            workoutFound = true;
                            break;
                        }
                    } catch (Exception e) {
                        //
                    }
                }

                if (!workoutFound && isAdded() && getContext() != null) {Toast.makeText(getContext(), R.string.could_not_find_the_workout_record_to_update, Toast.LENGTH_SHORT).show();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded() && getContext() != null) { Toast.makeText(getContext(), getString(R.string.error_updating_workout) + error.getMessage(), Toast.LENGTH_SHORT).show();}}
        });
    }
    private void navigateToCircularMetricDialog(Workout workout) {
        try {
            if (!isAdded() || getView() == null) {
                return;
            }

            Bundle args = new Bundle();
            args.putInt("workoutId", workout.getWorkoutId());
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_fragmentMain_to_addMetricDialogFragment, args);
        } catch (Exception e) {
            if (getActivity() != null && isAdded()) {
                try {
                    CircularMetricDialogFragment dialogFragment = new CircularMetricDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("workoutId", workout.getWorkoutId());
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getParentFragmentManager(), "CircularMetricDialog");
                } catch (Exception ex) {
                    //
                }
            }
        }
    }
        private void setupClickListeners() {
        btnToPersonalArea.setOnClickListener(v -> navigateToPersonalArea());
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            selectedCalendar.set(Calendar.HOUR_OF_DAY, 12);
            selectedDate = selectedCalendar.getTime();

            updateWorkoutButtons();
            loadWorkoutsForSelectedDate();
        });


        btnAddWorkout.setOnClickListener(v -> showAddWorkoutDialog());
        btnCancelWorkout.setOnClickListener(v -> showCancelWorkoutDialog());
    }

    private void loadWorkoutsForSelectedDate() {
        // First check if the fragment is still attached to avoid context-related crashes
        if (!isAdded()) {
            return;
        }

        // Check if selectedDate is null
        if (selectedDate == null) {
            if (selectedDateWorkouts != null) {
                selectedDateWorkouts.setVisibility(View.GONE);
            }
            if (noWorkoutsText != null) {
                noWorkoutsText.setVisibility(View.VISIBLE);
            }
            return;
        }

        // Remove previous listener if it exists to prevent memory leaks
        if (selectedDateWorkoutsListener != null) {
            try {
                FirebaseManager.getInstance().removeWorkoutsByDateListener(selectedDateWorkoutsListener);
            } catch (Exception e) {
                Log.e("FragmentMain", "Error removing previous listener: " + e.getMessage());
            }
        }

        // Create new listener
        selectedDateWorkoutsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check again if fragment is attached before proceeding with UI updates
                if (!isAdded()) {
                    return;
                }

                try {
                    List<WorkoutRecord> workouts = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            WorkoutRecord record = snapshot.getValue(WorkoutRecord.class);
                            if (record != null) {
                                if (record.getWorkout() != null) {
                                    workouts.add(record);
                                } else {
                                    Log.w("FragmentMain", "Workout object is null in record");
                                }
                            }
                        } catch (Exception e) {
                            Log.e("FragmentMain", "Error parsing workout record: " + e.getMessage());
                        }
                    }

                    // Update UI based on results - with null checks
                    if (workouts.isEmpty()) {
                        if (selectedDateWorkouts != null) {
                            selectedDateWorkouts.setVisibility(View.GONE);
                        }
                        if (noWorkoutsText != null) {
                            noWorkoutsText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (workoutsAdapter != null) {
                            workoutsAdapter.setWorkouts(workouts);
                        }
                        if (selectedDateWorkouts != null) {
                            selectedDateWorkouts.setVisibility(View.VISIBLE);
                        }
                        if (noWorkoutsText != null) {
                            noWorkoutsText.setVisibility(View.GONE);
                        }
                    }

                    // Update workout buttons state
                    updateWorkoutButtons();
                } catch (Exception e) {
                    Log.e("FragmentMain", "Error processing workout data: " + e.getMessage());
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Error loading workouts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Check if fragment is attached before UI updates
                if (!isAdded()) {
                    return;
                }

                Log.e("FragmentMain", "Database error: " + error.getMessage());

                // Set UI elements to proper states
                if (selectedDateWorkouts != null) {
                    selectedDateWorkouts.setVisibility(View.GONE);
                }
                if (noWorkoutsText != null) {
                    noWorkoutsText.setVisibility(View.VISIBLE);
                }

                // Show error toast if context is available
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load workouts: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Add the listener with proper error handling
        try {
            FirebaseManager.getInstance().getWorkoutsByDate(selectedDate, selectedDateWorkoutsListener);
        } catch (Exception e) {
            Log.e("FragmentMain", "Error setting up workouts listener: " + e.getMessage());
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Error loading workouts: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedDate != null) {
            loadWorkoutsForSelectedDate();
        }
    }

    private void loadUserData() {
        upcomingWorkoutAlert.setVisibility(View.GONE);
        TextView alertText = upcomingWorkoutAlert.findViewById(R.id.alert_text);

        try {
            // Create and store the listener
            // Check if fragment is still attached
            //
            ValueEventListener upcomingWorkoutsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if fragment is still attached
                    if (!isAdded()) return;

                    boolean hasUpcomingWorkout = false;
                    WorkoutRecord nextWorkout = null;
                    long earliestTime = Long.MAX_VALUE;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            WorkoutRecord record = snapshot.getValue(WorkoutRecord.class);
                            if (record != null && record.getWorkout() != null && record.getDate() > System.currentTimeMillis()) {
                                if (record.getDate() < earliestTime) {
                                    earliestTime = record.getDate();
                                    nextWorkout = record;
                                    hasUpcomingWorkout = true;
                                }
                            }
                        } catch (Exception e) {
                            //
                        }
                    }

                    if (hasUpcomingWorkout && isAdded()) {
                        showUpcomingWorkoutAlert(nextWorkout.getWorkout(), new Date(nextWorkout.getDate()));
                    } else {
                        if (alertText != null && isAdded()) {
                            alertText.setText(R.string.no_upcoming_workouts_scheduled);
                            upcomingWorkoutAlert.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded() && upcomingWorkoutAlert != null) {
                        upcomingWorkoutAlert.setVisibility(View.GONE);
                    }
                }
            };

            // Add the listener
            FirebaseManager.getInstance().getUpcomingWorkouts(upcomingWorkoutsListener);
        } catch (Exception e) {
            if (upcomingWorkoutAlert != null) {
                upcomingWorkoutAlert.setVisibility(View.GONE);
            }
        }
    }

    private void showUpcomingWorkoutAlert(Workout workout, Date workoutDate) {
        if (!isAdded() || workout == null) {
            if (upcomingWorkoutAlert != null) {
                upcomingWorkoutAlert.setVisibility(View.GONE);
            }
            return;
        }

        if (upcomingWorkoutAlert != null) {
            upcomingWorkoutAlert.setVisibility(View.VISIBLE);
            TextView alertText = upcomingWorkoutAlert.findViewById(R.id.alert_text);

            if (alertText != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                String formattedDate = dateFormat.format(workoutDate);
                String alertMessage = String.format(getString(R.string.upcoming_workout), workout.getName(), formattedDate);
                alertText.setText(alertMessage);
            }

            upcomingWorkoutAlert.setOnClickListener(v -> {
                calendarView.setDate(workoutDate.getTime());
                selectedDate = workoutDate;
                loadWorkoutsForSelectedDate();
            });
        }
    }
    private void updateWorkoutButtons() {
        if (selectedDate == null) {
            btnAddWorkout.setEnabled(false);
            btnCancelWorkout.setEnabled(false);
            return;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        selectedCal.set(Calendar.HOUR_OF_DAY, 0);
        selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        boolean isDateValid = selectedCal.getTimeInMillis() >= today.getTimeInMillis();

        if (!isDateValid) {
            btnAddWorkout.setEnabled(false);
            btnCancelWorkout.setEnabled(false);
            return;
        }

        checkIfDateHasWorkout(selectedDate, hasWorkout -> {
            btnAddWorkout.setEnabled(true);
            btnCancelWorkout.setEnabled(hasWorkout);
        });
    }

    private void navigateToPersonalArea() {
        try {
            Navigation.findNavController(requireView()).navigate(R.id.action_fragmentMain_to_fragmentPersonalArea);
        } catch (Exception e) {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, getString(R.string.failed_to_navigate) + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAddWorkoutDialog() {
        Context context = getContext();
        if (context != null) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.add_workout)
                    .setMessage(R.string.choose_workout_type).setPositiveButton(R.string.choose_from_existing_workouts, (dialog, which) -> {
                        Bundle args = new Bundle();
                        args.putLong(getString(R.string.selectedDate), selectedDate.getTime()); // Pass the selected date
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_fragmentMain_to_fragmentWorkouts, args);
                    }).setNegativeButton(R.string.create_custom_workout, (dialog, which) -> showCustomWorkoutDialog())
                    .show();
        }
    }
    private void showCustomWorkoutDialog() {
        try {
            Bundle args = new Bundle();
            // Don't use String.valueOf - it converts resource ID to a string like "2131951720"
            args.putLong(getString(R.string.selectedDate), selectedDate.getTime());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_fragmentMain_to_customWorkoutDialogFragment, args);
        } catch (Exception e) {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), getString(R.string.error_opening_custom_workout) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCancelWorkoutDialog() {
        Context context = getContext();
        if (context != null) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.cancel_workout)
                    .setMessage(R.string.are_you_sure_you_want_to_cancel_this_workout)
                    .setPositiveButton(R.string.yes, (dialog, which) -> cancelWorkout())
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    }
    private void cancelWorkout() {
        if (selectedDate != null) {
            FirebaseManager.getInstance().getWorkoutsByDate(selectedDate, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {Toast.makeText(getContext(), R.string.cancel_workout, Toast.LENGTH_SHORT).show();})
                                .addOnFailureListener(e -> Toast.makeText(getContext(), R.string.failed_to_cancel_workout, Toast.LENGTH_SHORT).show());
                    }

                    updateWorkoutButtons();
                    loadWorkoutsForSelectedDate();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), getString(R.string.error) + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void checkIfDateHasWorkout(Date date, OnWorkoutCheckListener listener) {
        FirebaseManager.getInstance().getWorkoutsByDate(date, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<WorkoutRecord> workouts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WorkoutRecord record = snapshot.getValue(WorkoutRecord.class);
                    if (record != null) {
                        workouts.add(record);
                    }
                }

                if (workouts.isEmpty()) {
                    selectedDateWorkouts.setVisibility(View.GONE);
                    noWorkoutsText.setVisibility(View.VISIBLE);
                } else {
                    workoutsAdapter.setWorkouts(workouts);
                    selectedDateWorkouts.setVisibility(View.VISIBLE);
                    noWorkoutsText.setVisibility(View.GONE);
                }

                listener.onResult(!workouts.isEmpty());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                selectedDateWorkouts.setVisibility(View.GONE);
                noWorkoutsText.setVisibility(View.GONE);
                listener.onResult(false);
            }
        });
    }

    interface OnWorkoutCheckListener {
        void onResult(boolean hasWorkout);
    }
}
