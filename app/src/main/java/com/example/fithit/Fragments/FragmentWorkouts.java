//package com.example.fithit.Fragments;
//
//import static com.example.fithit.R.*;
//
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//import com.example.fithit.Adapters.WorkoutsAdapter;
//import com.example.fithit.Managers.FirebaseManager;
//import com.example.fithit.Models.DatabaseWorkouts;
//import com.example.fithit.Models.Workout;
//import com.example.fithit.Models.WorkoutRecord;
//import com.example.fithit.R;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//public class FragmentWorkouts extends Fragment {
//
//    private Date selectedDate;
//    private static final String TAG = "FragmentWorkouts";
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_workouts, container, false);
//
//        if (getArguments() != null) {
//            long dateInMillis = getArguments().getLong("selectedDate");
//            selectedDate = new Date(dateInMillis);
//        }
//
//        RecyclerView workoutsRecyclerView = view.findViewById(R.id.workouts_recycler_view);
//        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        List<Workout> workoutsList = DatabaseWorkouts.getAllWorkouts();
//        WorkoutsAdapter workoutsAdapter = new WorkoutsAdapter(workoutsList, this::openWorkoutDetails);
//        workoutsRecyclerView.setAdapter(workoutsAdapter);
//
//        return view;
//    }
//
//    private void openWorkoutDetails(Workout workout) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(selectedDate);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        Date normalizedDate = calendar.getTime();
//
//        WorkoutRecord workoutRecord = new WorkoutRecord(workout, normalizedDate);
//
//        workoutRecord.setCompleted(false);
//
//        FirebaseManager.getInstance()
//                .addWorkoutRecord(workoutRecord)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(getContext(),
//                            R.string.the_workout_was_successfully_added_to_the_calendar,Toast.LENGTH_SHORT).show();
//
//                    NavController navController = Navigation.findNavController(requireView());
//                    navController.navigateUp();
//
//                })
//                .addOnFailureListener(e -> Toast.makeText(getContext(),
//                        getString(string.failed_to_save_workout) + e.getMessage(),
//                        Toast.LENGTH_SHORT).show());
//    }
//}
package com.example.fithit.Fragments;

import static com.example.fithit.R.*;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.fithit.Adapters.WorkoutsAdapter;
import com.example.fithit.Managers.FirebaseManager;
import com.example.fithit.Models.DatabaseWorkouts;
import com.example.fithit.Models.Workout;
import com.example.fithit.Models.WorkoutRecord;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentWorkouts extends Fragment {

    private Date selectedDate;
    private static final String TAG = "FragmentWorkouts";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_workouts, container, false);

        try {
            // Get selected date from arguments with proper error handling
            if (getArguments() != null) {
                // Use getString method to get the correct key
                long dateInMillis = getArguments().getLong(getString(string.selectedDate), System.currentTimeMillis());
                selectedDate = new Date(dateInMillis);
                Log.d(TAG, "Selected date: " + selectedDate);
            } else {
                selectedDate = new Date(); // Default to today if no date provided
                Log.d(TAG, "No date provided, using current date: " + selectedDate);
            }

            RecyclerView workoutsRecyclerView = view.findViewById(id.workouts_recycler_view);
            workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            List<Workout> workoutsList = DatabaseWorkouts.getAllWorkouts();
            Log.d(TAG, "Loaded " + workoutsList.size() + " workouts");

            WorkoutsAdapter workoutsAdapter = new WorkoutsAdapter(workoutsList, this::openWorkoutDetails);
            workoutsRecyclerView.setAdapter(workoutsAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Error loading workouts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void openWorkoutDetails(Workout workout) {
        try {
            if (!isAdded()) {
                Log.e(TAG, "Fragment not attached when trying to open workout details");
                return;
            }

            if (workout == null) {
                Log.e(TAG, "Attempted to open details for null workout");
                Toast.makeText(getContext(), getString(string.error_workout_data_is_missing), Toast.LENGTH_SHORT).show();
                return;
            }

            // Verify that the workout has required data
            if (workout.getExercises() == null || workout.getExercises().isEmpty()) {
                Log.e(TAG, "Workout has no exercises: " + workout.getName());
                Toast.makeText(getContext(), "Selected workout has no exercises", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Opening workout details for: " + workout.getName());

            // Normalize the date (set to midnight)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date normalizedDate = calendar.getTime();

            // Create workout record
            WorkoutRecord workoutRecord = new WorkoutRecord(workout, normalizedDate);
            workoutRecord.setCompleted(false);

            Log.d(TAG, "Saving workout record for date: " + normalizedDate);

            // Save to Firebase with proper error handling
            FirebaseManager.getInstance()
                    .addWorkoutRecord(workoutRecord)
                    .addOnSuccessListener(aVoid -> {
                        if (!isAdded()) return;

                        Log.d(TAG, "Workout saved successfully");
                        Toast.makeText(getContext(),
                                getString(string.the_workout_was_successfully_added_to_the_calendar),
                                Toast.LENGTH_SHORT).show();

                        try {
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigateUp();
                        } catch (Exception e) {
                            Log.e(TAG, "Navigation error after saving workout", e);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!isAdded()) return;

                        Log.e(TAG, "Failed to save workout", e);
                        Toast.makeText(getContext(),
                                getString(string.failed_to_save_workout) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in openWorkoutDetails", e);
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Error adding workout: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}