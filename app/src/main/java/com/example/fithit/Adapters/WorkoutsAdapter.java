package com.example.fithit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Models.Workout;
import com.example.fithit.R;

import java.util.List;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder> {
    private List<Workout> workouts;
    private OnWorkoutSelectedListener listener;

    public interface OnWorkoutSelectedListener {
        void onWorkoutSelected(Workout workout);
    }

    public WorkoutsAdapter(List<Workout> workouts, OnWorkoutSelectedListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }
    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView descriptionText;
        private TextView durationText;
        private TextView difficultyText;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.workout_name);
            descriptionText = itemView.findViewById(R.id.workout_description);
            durationText = itemView.findViewById(R.id.workout_duration);
            difficultyText = itemView.findViewById(R.id.workout_difficulty);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onWorkoutSelected(workouts.get(position));
                }
            });
        }

        public void bind(Workout workout) {
            nameText.setText(workout.getName());
            descriptionText.setText(workout.getDescription());
            durationText.setText(workout.getEstimatedDuration() + " minutes");
            difficultyText.setText(workout.getDifficultyLevel().toString());
        }
    }
}
