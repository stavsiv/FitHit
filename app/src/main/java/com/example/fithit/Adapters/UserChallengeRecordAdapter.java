package com.example.fithit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Models.Challenge;
import com.example.fithit.Models.ChallengeRecord;
import com.example.fithit.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserChallengeRecordAdapter extends RecyclerView.Adapter<UserChallengeRecordAdapter.ViewHolder> {

    private List<ChallengeRecord> challengeRecords;
    private OnChallengeActionListener listener;

    public interface OnChallengeActionListener {
        void onChallengeRemove(ChallengeRecord record);
        void onChallengeRenew(ChallengeRecord record);
    }

    public UserChallengeRecordAdapter(List<ChallengeRecord> challengeRecords) {
        this.challengeRecords = challengeRecords;
    }

    public void setOnChallengeActionListener(OnChallengeActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChallengeRecord record = challengeRecords.get(position);
        Challenge challenge = record.getChallenge();

        if (challenge == null) return;

        // Set basic information
        holder.tvTitle.setText(challenge.getName());
        holder.tvDescription.setText(challenge.getDescription());
        holder.tvPoints.setText(challenge.getHeartsReward() + " ❤️");

        int currentProgress = record.getCurrentProgress();
        int targetValue = challenge.getTargetValue();

        holder.progressBar.setMax(targetValue);
        holder.progressBar.setProgress(currentProgress);

        if (record.isCompleted()) {
            String progressText = "Completed! " + targetValue + "/" + targetValue;
            holder.tvProgress.setText(progressText);
            holder.tvStatus.setText("Completed ✓");
            holder.tvStatus.setVisibility(View.VISIBLE);

            holder.progressBar.setProgressTintList(holder.itemView.getContext().getResources().getColorStateList(android.R.color.holo_green_dark));

            if (holder.btnRenew != null) {
                holder.btnRenew.setVisibility(View.GONE);
            }
        } else if (record.isExpired()) {
            holder.tvStatus.setText("Expired");
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));

            if (holder.btnRenew != null) {
                holder.btnRenew.setVisibility(View.VISIBLE);
                holder.btnRenew.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onChallengeRenew(record);
                    }
                });
            }
        } else {
            int remaining = targetValue - currentProgress;
            String progressText = "Progress: " + currentProgress + "/" + targetValue;
            if (remaining > 0) {
                progressText += " (" + remaining + " " + getUnitLabel(challenge) + " left)";
            }
            holder.tvProgress.setText(progressText);
            holder.tvStatus.setVisibility(View.GONE);

            if (holder.btnRenew != null) {
                holder.btnRenew.setVisibility(View.GONE);
            }
        }

        // Set expiration date if available
        if (record.getEndDate() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String endDate = "Valid until: " + dateFormat.format(new Date(record.getEndDate()));
            holder.tvExpiration.setText(endDate);

            // Check if challenge is about to expire
            int daysRemaining = record.getDaysRemaining();
            if (daysRemaining <= 2 && daysRemaining >= 0 && !record.isCompleted()) {
                holder.tvExpiration.setTextColor(
                        holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
            } else {
                holder.tvExpiration.setTextColor(
                        holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            }
        } else {
            holder.tvExpiration.setVisibility(View.GONE);
        }

        // Remove button functionality
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                int removePosition = holder.getBindingAdapterPosition();
                if (removePosition != RecyclerView.NO_POSITION) {
                    ChallengeRecord recordToRemove = challengeRecords.get(removePosition);

                    listener.onChallengeRemove(recordToRemove);
                }
            }
        });
    }
    public int removeItem(ChallengeRecord record) {
        if (record == null || challengeRecords == null || record.getChallenge() == null) return -1;

        String challengeNameToRemove = record.getChallenge().getName();

        for (int i = 0; i < challengeRecords.size(); i++) {
            if (challengeRecords.get(i).getChallenge() != null &&
                    challengeNameToRemove.equals(challengeRecords.get(i).getChallenge().getName())) {
                challengeRecords.remove(i);
                notifyItemRemoved(i);
                return i;
            }
        }

        return -1;
    }
    private String getUnitLabel(Challenge challenge) {
        String challengeName = challenge.getName();

        // Handle all challenge types
        switch (challengeName) {
            case "Daily Workout Champion":
                return "workout";
            case "Workout Warrior":
            case "Fitness Journey":
                return "workouts";
            case "Strength Builder":
                return "strength workouts";
            case "Cardio Master":
                return "cardio workouts";
            case "Core Power":
                return "core workouts";
            case "Expert Challenger":
                return "expert workouts";
            case "Exercise Variety":
                return "muscle groups";
            case "Full Body Focus":
                return "full body workouts";
            case "Consistency King":
                return "weekly goals";
        }

        return "items";
    }

    @Override
    public int getItemCount() {
        return challengeRecords.size();
    }

    public void updateChallenges(List<ChallengeRecord> newRecords) {
        this.challengeRecords = newRecords;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPoints;
        TextView tvProgress;
        TextView tvStatus;
        TextView tvExpiration;
        ProgressBar progressBar;
        Button btnRemove;
        Button btnRenew;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvUserChallengeTitle);
            tvDescription = view.findViewById(R.id.tvUserChallengeDescription);
            tvPoints = view.findViewById(R.id.tvUserChallengePoints);
            tvProgress = view.findViewById(R.id.tvUserChallengeProgress);
            tvStatus = view.findViewById(R.id.tvUserChallengeStatus);
            tvExpiration = view.findViewById(R.id.tvUserChallengeExpiration);
            progressBar = view.findViewById(R.id.progressUserChallenge);
            btnRemove = view.findViewById(R.id.btnRemoveUserChallenge);
        }
    }
}