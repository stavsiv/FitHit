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
    // UI Components
    private MaterialCardView btnToPersonalArea;
    private MaterialCardView upcomingWorkoutAlert;
    private CalendarView calendarView;
    private MaterialButton btnAddWorkout;
    private MaterialButton btnCancelWorkout;
    private Date selectedDate;

    private RecyclerView selectedDateWorkouts;
    private TextView noWorkoutsText;
    private UserWorkoutsAdapter workoutsAdapter;

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
        // Initialize all UI components
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
                            "Duration: " + workoutObj.getEstimatedDuration() + " minutes\n" +
                            "Difficulty level: " + workoutObj.getDifficultyLevel() + "\n" +
                            "Status: " + (workout.isCompleted() ? "Completed ✓" : "Not completed"))
                    .setPositiveButton("Close", null);

            if (!workout.isCompleted()) {
                builder.setNeutralButton("Mark as Completed", (dialog, which) -> {
                    markWorkoutAsCompleted(workout);
                });
            }

            builder.show();
        }
    }
    private void markWorkoutAsCompleted(WorkoutRecord workout) {
        if (workout == null || workout.getWorkout() == null) {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, "Error: Workout data is missing", Toast.LENGTH_SHORT).show();
            }
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

                            final Workout workoutToNavigate = workout.getWorkout(); // שמירת הפנייה לאימון לפני הקולבק

                            snapshot.getRef().child("completed").setValue(true)
                                    .addOnSuccessListener(aVoid -> {
                                        if (isAdded() && getContext() != null) {
                                            Toast.makeText(getContext(), "Workout marked as completed!", Toast.LENGTH_SHORT).show();
                                            loadWorkoutsForSelectedDate();

                                            navigateToCircularMetricDialog(workoutToNavigate);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (isAdded() && getContext() != null) {
                                            Toast.makeText(getContext(), "Failed to update workout: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            workoutFound = true;
                            break;
                        }
                    } catch (Exception e) {
                        Log.e("FragmentMain", "Error finding workout to update: " + e.getMessage());
                    }
                }

                if (!workoutFound && isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Could not find the workout record to update", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error updating workout: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void navigateToCircularMetricDialog(Workout workout) {
        try {
            if (!isAdded() || getView() == null) {
                Log.w("FragmentMain", "Fragment not attached or view is null. Skipping navigation.");
                return;
            }

            Bundle args = new Bundle();
            args.putInt("workoutId", workout.getWorkoutId());
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_fragmentMain_to_addMetricDialogFragment, args);
        } catch (Exception e) {
            Log.e("FragmentMain", "Navigation error: " + e.getMessage());

            if (getActivity() != null && isAdded()) {
                try {
                    CircularMetricDialogFragment dialogFragment = new CircularMetricDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("workoutId", workout.getWorkoutId());
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getParentFragmentManager(), "CircularMetricDialog");
                } catch (Exception ex) {
                    Log.e("FragmentMain", "Failed to show dialog directly: " + ex.getMessage());
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
        if (selectedDate == null) {
            Log.d("WorkoutDebug", "Selected date is null");
            selectedDateWorkouts.setVisibility(View.GONE);
            noWorkoutsText.setVisibility(View.VISIBLE);
            return;
        }

        Log.d("WorkoutDebug", "Loading workouts for date: " + selectedDate);

        FirebaseManager.getInstance().getWorkoutsByDate(selectedDate, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<WorkoutRecord> workouts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        WorkoutRecord record = snapshot.getValue(WorkoutRecord.class);
                        if (record != null) {
                            if (record.getWorkout() != null) {
                                workouts.add(record);
                                try {
                                    Log.d("WorkoutDebug", "Found workout: " + record.getWorkout().getName() +
                                            " for date: " + new Date(record.getDate()));
                                } catch (Exception e) {
                                    Log.e("WorkoutDebug", "Error accessing workout details: " + e.getMessage());
                                }
                            } else {
                                Log.w("WorkoutDebug", "Found record with null workout for date: " +
                                        new Date(record.getDate()));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("WorkoutDebug", "Error processing workout record: " + e.getMessage());
                    }
                }

                if (workouts.isEmpty()) {
                    Log.d("WorkoutDebug", "No workouts found for date: " + selectedDate);
                    selectedDateWorkouts.setVisibility(View.GONE);
                    noWorkoutsText.setVisibility(View.VISIBLE);
                } else {
                    Log.d("WorkoutDebug", "Found " + workouts.size() + " workouts");
                    workoutsAdapter.setWorkouts(workouts);
                    selectedDateWorkouts.setVisibility(View.VISIBLE);
                    noWorkoutsText.setVisibility(View.GONE);
                }

                updateWorkoutButtons();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("WorkoutDebug", "Error loading workouts: " + error.getMessage());
                selectedDateWorkouts.setVisibility(View.GONE);
                noWorkoutsText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedDate != null) {
            loadWorkoutsForSelectedDate();
        }
    }
    private void loadUserData() {
        // Set alert as initially hidden until we check if there are upcoming workouts
        upcomingWorkoutAlert.setVisibility(View.GONE);
        TextView alertText = upcomingWorkoutAlert.findViewById(R.id.alert_text);

        try {
            FirebaseManager.getInstance().getUpcomingWorkouts(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean hasUpcomingWorkout = false;
                    WorkoutRecord nextWorkout = null;
                    long earliestTime = Long.MAX_VALUE;

                    // Find the earliest upcoming workout
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            WorkoutRecord record = snapshot.getValue(WorkoutRecord.class);
                            if (record != null && record.getWorkout() != null && record.getDate() > System.currentTimeMillis()) {
                                // Check if this is earlier than our current earliest
                                if (record.getDate() < earliestTime) {
                                    earliestTime = record.getDate();
                                    nextWorkout = record;
                                    hasUpcomingWorkout = true;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("FragmentMain", "Error processing upcoming workout: " + e.getMessage());
                        }
                    }

                    if (hasUpcomingWorkout && nextWorkout != null) {
                        showUpcomingWorkoutAlert(nextWorkout.getWorkout(), new Date(nextWorkout.getDate()));
                    } else {
                        // No upcoming workouts
                        if (alertText != null) {
                            alertText.setText("No upcoming workouts scheduled");
                            upcomingWorkoutAlert.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("FirebaseError", "Failed to get upcoming workouts: " + error.getMessage());
                    upcomingWorkoutAlert.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e("FragmentMain", "Error in loadUserData: " + e.getMessage());
            upcomingWorkoutAlert.setVisibility(View.GONE);
        }
    }

    private void showUpcomingWorkoutAlert(Workout workout, Date workoutDate) {
        if (workout == null) {
            Log.e("FragmentMain", "Cannot show alert for null workout");
            upcomingWorkoutAlert.setVisibility(View.GONE);
            return;
        }

        upcomingWorkoutAlert.setVisibility(View.VISIBLE);
        TextView alertText = upcomingWorkoutAlert.findViewById(R.id.alert_text);

        if (alertText != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            String formattedDate = dateFormat.format(workoutDate);
            String alertMessage = String.format("Upcoming workout: %s on %s", workout.getName(), formattedDate);
            alertText.setText(alertMessage);
        }

        upcomingWorkoutAlert.setOnClickListener(v -> {
            calendarView.setDate(workoutDate.getTime());
            selectedDate = workoutDate;
            loadWorkoutsForSelectedDate();
        });
    }
   /* @SuppressLint("DefaultLocale")
    private void loadUserData() {
        // במקום להשתמש בפונקציה המקורית, פשוט הסתר את ההתראה
        upcomingWorkoutAlert.setVisibility(View.GONE);

        // אופציה: עדיין לנסות לטעון את הנתונים, אבל עם גישה מגוננת יותר
        try {
            FirebaseManager.getInstance().getUpcomingWorkouts(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // אל תעשה כלום עם הנתונים בינתיים, רק צא מהפונקציה
                    return;

                *//*
                // הקוד המקורי שגורם לשגיאה - מבוטל
                boolean hasUpcomingWorkout = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        WorkoutRecord record = snapshot.getValue(WorkoutRecord.class);
                        if (record != null && record.getWorkout() != null) {
                            Workout workout = record.getWorkout();
                            showUpcomingWorkoutAlert(workout);
                            hasUpcomingWorkout = true;
                            break;
                        }
                    } catch (Exception e) {
                        Log.e("FragmentMain", "Error processing upcoming workout: " + e.getMessage());
                    }
                }

                if (!hasUpcomingWorkout) {
                    upcomingWorkoutAlert.setVisibility(View.GONE);
                }
                *//*
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("FirebaseError", "Failed to get upcoming workouts: " + error.getMessage());
                    upcomingWorkoutAlert.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e("FragmentMain", "Error in loadUserData: " + e.getMessage());
        }
    }*/

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
            btnAddWorkout.setEnabled(!hasWorkout);
            btnCancelWorkout.setEnabled(hasWorkout);

            // Debug log
            Log.d("WorkoutButtons", "Selected date: " + selectedDate);
            Log.d("WorkoutButtons", "Is date valid: " + isDateValid);
            Log.d("WorkoutButtons", "Has workout: " + hasWorkout);
            Log.d("WorkoutButtons", "Add button enabled: " + btnAddWorkout.isEnabled());
        });
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
                        args.putLong("selectedDate", selectedDate.getTime()); // Pass the selected date
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_fragmentMain_to_fragmentWorkouts, args);
                    })
                    .setNegativeButton("Create custom workout", (dialog, which) -> showCustomWorkoutDialog())
                    .show();
        }
    }

    private void showCustomWorkoutDialog() {
        try {
            Bundle args = new Bundle();
            args.putLong("selectedDate", selectedDate.getTime());
            Log.d("Navigation", "Navigating to CustomWorkoutDialogFragment with date: " + selectedDate);
            Navigation.findNavController(getView())
                    .navigate(R.id.action_fragmentMain_to_customWorkoutDialogFragment, args);
        } catch (Exception e) {
            Log.e("Navigation", "Failed to navigate to custom workout dialog", e);
            Toast.makeText(getContext(),
                    "Error opening custom workout: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showCancelWorkoutDialog() {
        Context context = getContext();
        if (context != null) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Cancel Workout")
                    .setMessage("Are you sure you want to cancel this workout?")
                    .setPositiveButton("Yes", (dialog, which) -> cancelWorkout())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void cancelWorkout() {
        if (selectedDate != null) {
            FirebaseManager.getInstance().getWorkoutsByDate(selectedDate, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Workout cancelled", Toast.LENGTH_SHORT).show();
                                    updateWorkoutButtons();
                                    loadWorkoutsForSelectedDate();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Failed to cancel workout", Toast.LENGTH_SHORT).show()
                                );
                        break; // Cancel only the first workout on this date
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void checkIfDateHasWorkout(Date date, OnWorkoutCheckListener listener) {
        FirebaseManager.getInstance().getWorkoutsByDate(date, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            public void onCancelled(DatabaseError error) {
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
