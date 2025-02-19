package com.example.fithit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Enums.EquipmentType;
import com.example.fithit.Models.Exercise;
import com.example.fithit.R;

import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises;

    public ExercisesAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseName;
        private TextView exerciseType;
        private TextView exerciseReps;
        private TextView exerciseEquipment;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseType = itemView.findViewById(R.id.exercise_type);
            exerciseReps = itemView.findViewById(R.id.exercise_reps);
            exerciseEquipment = itemView.findViewById(R.id.exercise_equipment);
        }

        public void bind(Exercise exercise) {
            exerciseName.setText(exercise.getExerciseName());
            exerciseType.setText(exercise.getExerciseType().toString());
            exerciseReps.setText(exercise.getRepetitionsForDifficulty(exercise.getDifficultyLevel()) + " reps");

            List<EquipmentType> equipment = exercise.getRequiredEquipmentTypes();
            if (!equipment.isEmpty()) {
                exerciseEquipment.setText(equipment.toString());
                exerciseEquipment.setVisibility(View.VISIBLE);
            } else {
                exerciseEquipment.setVisibility(View.GONE);
            }
        }
    }
}
