package com.example.fithit.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fithit.Adapters.ExercisesAdapter;
import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Models.DatabaseWorkouts;
import com.example.fithit.Models.Exercise;
import com.example.fithit.Models.Workout;
import com.example.fithit.R;
import com.google.android.material.chip.Chip;

import java.util.List;


public class FragmentWorkoutDetails extends Fragment {

    private Workout workout;
    private TextView workoutName;
    private TextView workoutDescription;
    private Chip workoutDuration;
    private Chip workoutDifficulty;
    private RecyclerView exercisesRecyclerView;
    private ExercisesAdapter exercisesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Assuming you pass the workout ID and fetch the workout from the database
            int workoutId = getArguments().getInt("workoutId");
            workout = DatabaseWorkouts.getWorkoutById(workoutId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_details, container, false);
        initializeViews(view);
        setupExercisesList();
        populateWorkoutDetails();
        return view;
    }

    private void initializeViews(View view) {
        workoutName = view.findViewById(R.id.workout_name);
        workoutDescription = view.findViewById(R.id.workout_description);
        workoutDuration = view.findViewById(R.id.workout_duration);
        workoutDifficulty = view.findViewById(R.id.workout_difficulty);
        exercisesRecyclerView = view.findViewById(R.id.exercises_recycler_view);
    }

    private void setupExercisesList() {
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exercisesAdapter = new ExercisesAdapter(workout.getExercises());
        exercisesRecyclerView.setAdapter(exercisesAdapter);
    }

    private void populateWorkoutDetails() {
        if (workout != null) {
            workoutName.setText(workout.getName());
            workoutDescription.setText(workout.getDescription());
            workoutDuration.setText(workout.getEstimatedDuration() + " minutes");
            workoutDifficulty.setText(workout.getDifficultyLevel().toString());
        }
    }
}
