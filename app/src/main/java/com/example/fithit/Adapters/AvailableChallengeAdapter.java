package com.example.fithit.Adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Models.Challenge;
import com.example.fithit.R;

import java.util.List;

public class AvailableChallengeAdapter extends RecyclerView.Adapter<AvailableChallengeAdapter.ViewHolder> {

    private List<Challenge> availableChallenges;
    private OnChallengeSelectedListener listener;

    public interface OnChallengeSelectedListener {
        void onChallengeSelected(Challenge challenge);
    }

    public AvailableChallengeAdapter(List<Challenge> availableChallenges) {
        this.availableChallenges = availableChallenges;
    }

    public void setOnChallengeSelectedListener(OnChallengeSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_available_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Challenge challenge = availableChallenges.get(position);

        // Set basic information
        holder.tvTitle.setText(challenge.getName());
        holder.tvDescription.setText(challenge.getDescription());
        holder.tvPoints.setText(challenge.getHeartsReward() + " ❤️");

        // Set difficulty with visual indicator
        String difficulty = challenge.getDifficulty();
        holder.tvDifficulty.setText("Difficulty: " + difficulty);

        if (holder.cardView != null) {
            int cardColor;
            switch (difficulty) {
                case "BEGINNER":
                    cardColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_light);
                    break;
                case "INTERMEDIATE":
                    cardColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light);
                    break;
                case "EXPERT":
                    cardColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_purple);
                    break;
                default:
                    cardColor = holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray);
                    break;
            }

            GradientDrawable drawable = new GradientDrawable();
            drawable.setStroke(4, cardColor); // 4dp stroke width
            drawable.setCornerRadius(8); // Optional: add corner radius
            holder.cardView.setBackground(drawable);
        }

        holder.tvChallengeType.setText("Type: " + challenge.getType());

        holder.tvTarget.setText("Target: " + challenge.getTargetValue() + " " + getTargetUnit(challenge));

        // Set duration
        int days = 0;
        switch (challenge.getType()) {
            case "DAILY":
                days = 1;
                break;
            case "WEEKLY":
                days = 7;
                break;
            case "MONTHLY":
                days = 30;
                break;
        }

        holder.tvDuration.setText("Duration: " + days + " day" + (days > 1 ? "s" : ""));

        // Add button click listener
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChallengeSelected(challenge);
            }
        });
    }

    @Override
    public int getItemCount() {
        return availableChallenges.size();
    }


    private String getTargetUnit(Challenge challenge) {
        String challengeName = challenge.getName();

        if (challengeName.contains("Steps")) {
            return "steps";
        } else if (challengeName.equals("Daily Workout Champion") ||
                challengeName.equals("Workout Warrior") ||
                challengeName.equals("Fitness Journey")) {
            return "workouts";
        } else if (challengeName.equals("Strength Builder")) {
            return "strength workouts";
        } else if (challengeName.equals("Cardio Master")) {
            return "cardio workouts";
        } else if (challengeName.equals("Core Power")) {
            return "core workouts";
        } else if (challengeName.equals("Expert Challenger")) {
            return "expert workouts";
        } else if (challengeName.equals("Exercise Variety")) {
            return "muscle groups";
        } else if (challengeName.equals("Full Body Focus")) {
            return "full body workouts";
        } else if (challengeName.equals("Consistency King")) {
            return "weekly goals";
        } else if (challengeName.contains("Hydration") || challengeName.contains("water")) {
            return "cups";
        }

        return "activities";
    }


    public void updateChallenges(List<Challenge> newChallenges) {
        this.availableChallenges = newChallenges;
        notifyDataSetChanged();
    }

    public void filterByType(String type) {
        if (type == null || type.isEmpty() || type.equals("ALL")) {
            notifyDataSetChanged();
            return;
        }

        this.availableChallenges = Challenge.getChallengesByType(type);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPoints;
        TextView tvDifficulty;
        TextView tvTarget;
        TextView tvChallengeType;
        TextView tvDuration;
        Button btnAdd;
        CardView cardView;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvAvailableChallengeTitle);
            tvDescription = view.findViewById(R.id.tvAvailableChallengeDescription);
            tvPoints = view.findViewById(R.id.tvAvailableChallengePoints);
            tvDifficulty = view.findViewById(R.id.tvAvailableChallengeDifficulty);
            tvTarget = view.findViewById(R.id.tvAvailableChallengeTarget);
            tvChallengeType = view.findViewById(R.id.tvAvailableChallengeType);
            tvDuration = view.findViewById(R.id.tvAvailableChallengeDuration);
            btnAdd = view.findViewById(R.id.btnAddChallenge);
            cardView = view.findViewById(R.id.cardViewAvailableChallenge);
        }
    }
}