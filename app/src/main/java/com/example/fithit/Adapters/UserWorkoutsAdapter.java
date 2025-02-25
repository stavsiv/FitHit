package com.example.fithit.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;

import java.util.ArrayList;
import java.util.List;

public class UserWorkoutsAdapter extends RecyclerView.Adapter<UserWorkoutsAdapter.WorkoutViewHolder> {
    private List<WorkoutRecord> workouts = new ArrayList<>();
    private OnWorkoutClickListener listener;

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutRecord workoutRecord = workouts.get(position);
        holder.bind(workoutRecord);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWorkouts(List<WorkoutRecord> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    public void setOnWorkoutClickListener(OnWorkoutClickListener listener) {
        this.listener = listener;
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView workoutName;
        private final TextView workoutDescription;
        private final TextView workoutDuration;
        private final TextView workoutDifficulty;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            workoutDescription = itemView.findViewById(R.id.workout_description);
            workoutDuration = itemView.findViewById(R.id.workout_duration);
            workoutDifficulty = itemView.findViewById(R.id.workout_difficulty);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onWorkoutClick(workouts.get(position));
                }
            });
        }

        @SuppressLint("DefaultLocale")
        public void bind(WorkoutRecord workoutRecord) {
            if (workoutRecord == null || workoutRecord.getWorkout() == null) {
                return;
            }

            workoutName.setText(workoutRecord.getWorkout().getName());
            workoutDescription.setText(workoutRecord.getWorkout().getDescription());
            workoutDuration.setText(String.format("minutes %d", workoutRecord.getWorkout().getEstimatedDuration()));
            if (workoutRecord.getWorkout().getDifficultyLevel() != null) {
                String difficultyText = workoutRecord.getWorkout().getDifficultyLevel().toString();

                if (workoutRecord.isCompleted()) {
                    workoutDifficulty.setText(String.format("%s (completed)", difficultyText));
                    workoutDifficulty.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));

                } else {
                    workoutDifficulty.setText(difficultyText);
                    workoutDifficulty.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark, itemView.getContext().getTheme()));
                }
            }
        }
    }

    public interface OnWorkoutClickListener {
        void onWorkoutClick(WorkoutRecord workout);
    }
}