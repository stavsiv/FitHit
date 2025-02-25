package com.example.fithit.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fithit.Models.Challenge;
import com.example.fithit.Models.User;
import com.example.fithit.R;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {
    private List<Challenge> challenges;
    private User currentUser;
    public ChallengeAdapter(List<Challenge> challenges, User user) {  // עדכון הבנאי
        this.challenges = challenges;
        this.currentUser = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);

        holder.tvTitle.setText(challenge.getName());
        holder.tvDescription.setText(challenge.getDescription());
        holder.tvPoints.setText(challenge.getPointReward() + " Points");
        holder.checkbox.setChecked(challenge.isCompleted());

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!challenge.isCompleted()) {
                    challenge.checkCompletion(currentUser, challenge.getTargetValue());
                    notifyItemChanged(holder.getBindingAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPoints;
        CheckBox checkbox;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvChallengeTitle);
            tvDescription = view.findViewById(R.id.tvChallengeDescription);
            tvPoints = view.findViewById(R.id.tvChallengePoints);
            checkbox = view.findViewById(R.id.checkboxChallenge);
        }
    }
}