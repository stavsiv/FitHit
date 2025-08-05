package com.example.fithit.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fithit.Adapters.ExercisesAdapter;
import com.example.fithit.Managers.FirebaseManager;
import com.example.fithit.Models.Workout;
import com.example.fithit.R;

public class FragmentWorkoutDetails extends Fragment {

    private Workout workout;
    private TextView workoutName;
    private TextView workoutDescription;
    private TextView  workoutDuration;
    private TextView  workoutDifficulty;
    private RecyclerView exercisesRecyclerView;

    public FragmentWorkoutDetails() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            workout = (Workout) getArguments().getSerializable("workout");
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

        Button backToMainButton = view.findViewById(R.id.back_to_main_button);
        backToMainButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_fragmentWorkoutDetails_to_fragmentMain);
        });

        Button finishWorkoutButton = view.findViewById(R.id.finish_workout_button);
        finishWorkoutButton.setOnClickListener(v -> {
            addHeartsToUser();

            Bundle args = new Bundle();
            args.putInt("workoutId", workout.getWorkoutId());
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_fragmentWorkoutDetails_to_addMetricDialogFragment);
        });
    }
    private void addHeartsToUser() {
        if (workout != null) {
            int heartsEarned = workout.calculateHearts();

            FirebaseManager.getInstance()
                    .updateUserHearts(heartsEarned)
                    .addOnSuccessListener(aVoid -> {
                        String message = getString(R.string.you_earned) + heartsEarned + getString(R.string.hearts);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            getString(R.string.failed_to_update_hearts) + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        }
    }
    private void setupExercisesList() {
        if (workout == null || workout.getExercises() == null) {
            Log.e("WorkoutDetails", "Workout or exercises list is null");
            return;
        }
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ExercisesAdapter exercisesAdapter = new ExercisesAdapter(workout.getExercises());
        exercisesRecyclerView.setAdapter(exercisesAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void populateWorkoutDetails() {
        if (workout != null) {
            workoutName.setText(workout.getName());
            workoutDescription.setText(workout.getDescription());
            workoutDuration.setText(workout.getEstimatedDuration() + " minutes");
            workoutDifficulty.setText(workout.getDifficultyLevel().toString());
        }
    }
}
