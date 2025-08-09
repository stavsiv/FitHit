package com.example.fithit.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Adapters.AvailableChallengeAdapter;
import com.example.fithit.Managers.FirebaseManager;
import com.example.fithit.Models.Challenge;
import com.example.fithit.Models.ChallengeRecord;
import com.example.fithit.Models.User;
import com.example.fithit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AvailableChallengesDialog extends DialogFragment {

    private RecyclerView recyclerView;
    private ProgressBar progressLoading;
    private TextView tvNoAvailableChallenges;
    private final FirebaseManager firebaseManager;
    private AvailableChallengeAdapter adapter;
    private OnChallengeSelectedListener listener;

    public interface OnChallengeSelectedListener {
        void onChallengeSelected(Challenge challenge);
    }

    public AvailableChallengesDialog(User user) {
        this.firebaseManager = FirebaseManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_available_challenges, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAvailableChallenges);
        progressLoading = view.findViewById(R.id.progressLoadingChallenges);
        tvNoAvailableChallenges = view.findViewById(R.id.tvNoAvailableChallenges);

        ImageButton btnClose = view.findViewById(R.id.btnCloseAvailableChallengesDialog);
        btnClose.setOnClickListener(v -> dismiss());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableChallengeAdapter(new ArrayList<>());
        adapter.setOnChallengeSelectedListener(challenge -> {
            if (listener != null) {
                listener.onChallengeSelected(challenge);
                Toast.makeText(getContext(), getString(R.string.new_challenge_added) + challenge.getName(), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);

        loadAvailableChallenges();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void loadAvailableChallenges() {
        progressLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoAvailableChallenges.setVisibility(View.GONE);

        firebaseManager.getUserChallengeRecords()
                .addOnSuccessListener(userChallengeRecords -> {
                    List<Challenge> userChallenges = new ArrayList<>();
                    for (ChallengeRecord record : userChallengeRecords) {
                        if (record.getChallenge() != null) {
                            userChallenges.add(record.getChallenge());
                        }
                    }

                    List<Challenge> allChallenges = Challenge.ALL_CHALLENGES;

                    if (!isAdded()) return;
                    progressLoading.setVisibility(View.GONE);

                    List<Challenge> availableChallenges = filterOutExistingChallenges(
                            allChallenges, userChallenges);

                    if (availableChallenges.isEmpty()) {
                        tvNoAvailableChallenges.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoAvailableChallenges.setVisibility(View.GONE);
                        adapter.updateChallenges(availableChallenges);
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;

                    progressLoading.setVisibility(View.GONE);
                    tvNoAvailableChallenges.setVisibility(View.VISIBLE);
                    tvNoAvailableChallenges.setText(R.string.error_loading_challenges + e.getMessage());
                });
    }
    private List<Challenge> filterOutExistingChallenges(List<Challenge> allChallenges,
                                                        List<Challenge> userChallenges) {
        List<Challenge> filteredChallenges = new ArrayList<>();

        for (Challenge challenge : allChallenges) {
            boolean alreadyExists = false;

            for (Challenge userChallenge : userChallenges) {
                if (Objects.equals(challenge.getChallengeId(), userChallenge.getChallengeId())) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                filteredChallenges.add(challenge);
            }
        }

        return filteredChallenges;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }
    public void setOnChallengeSelectedListener(OnChallengeSelectedListener listener) {
        this.listener = listener;
    }
}