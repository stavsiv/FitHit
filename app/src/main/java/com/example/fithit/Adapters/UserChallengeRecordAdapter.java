package com.example.fithit.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
    private OnListEmptyListener emptyListener;
    public interface OnChallengeActionListener {
        void onChallengeRemove(ChallengeRecord record);
        void onChallengeRenew(ChallengeRecord record);
        void onChallengeCompleted(ChallengeRecord record);
    }
    public interface OnListEmptyListener {
        void onListEmpty();
    }

    public void setOnListEmptyListener(OnListEmptyListener listener) {
        this.emptyListener = listener;
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

        holder.tvTitle.setText(challenge.getName());
        holder.tvDescription.setText(challenge.getDescription());
        holder.tvPoints.setText(challenge.getHeartsReward() + " ❤️");

        int currentProgress = record.getCurrentProgress();
        int targetValue = challenge.getTargetValue();

        boolean canMarkComplete = currentProgress >= targetValue && !record.isCompleted() && !record.isExpired();

        holder.checkboxComplete.setChecked(record.isCompleted());
        holder.checkboxComplete.setEnabled(canMarkComplete);

        holder.checkboxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("ChallengeCheckbox", "Checkbox clicked: " + isChecked + " for challenge: " + challenge.getName());
            if (isChecked && canMarkComplete) {
                if (record.getCurrentProgress() >= challenge.getTargetValue()) {
                    // Update progress to target value to mark challenge as completed
                    record.updateProgress(challenge.getTargetValue());
                    record.setCompleted(true);
                    Log.d("ChallengeCheckbox", "Progress updated to: " + record.getCurrentProgress() + "/" + challenge.getTargetValue());

                    if (listener != null) {
                        listener.onChallengeCompleted(record);
                    } else {
                        Log.e("ChallengeCheckbox", "Listener is null");
                    }
                } else {
                    buttonView.setChecked(false);
                }
            }
        });

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

        if (record.getEndDate() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String endDate = "Valid until: " + dateFormat.format(new Date(record.getEndDate()));
            holder.tvExpiration.setText(endDate);

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
    public void removeCompletedChallenge(ChallengeRecord record) {
        int position = -1;
        for (int i = 0; i < challengeRecords.size(); i++) {
            if (challengeRecords.get(i).getChallenge() != null &&
                    record.getChallenge() != null &&
                    challengeRecords.get(i).getChallenge().getName().equals(
                            record.getChallenge().getName())) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            challengeRecords.remove(position);
            notifyItemRemoved(position);

            if (challengeRecords.isEmpty() && emptyListener != null) {
                emptyListener.onListEmpty();
            }
        }
    }

    private String getUnitLabel(Challenge challenge) {
        String challengeName = challenge.getName();

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
        CheckBox checkboxComplete;

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
            checkboxComplete = view.findViewById(R.id.checkboxChallengeComplete);
        }
    }
}