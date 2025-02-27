package com.example.fithit.Fragments;

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

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewAvailableChallenges);
        progressLoading = view.findViewById(R.id.progressLoadingChallenges);
        tvNoAvailableChallenges = view.findViewById(R.id.tvNoAvailableChallenges);

        // Set up close button
        ImageButton btnClose = view.findViewById(R.id.btnCloseAvailableChallengesDialog);
        btnClose.setOnClickListener(v -> dismiss());

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableChallengeAdapter(new ArrayList<>());
        adapter.setOnChallengeSelectedListener(challenge -> {
            if (listener != null) {
                listener.onChallengeSelected(challenge);
                Toast.makeText(getContext(),
                        "new challenge added: " + challenge.getName(),
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);

        loadAvailableChallenges();

        return view;
    }

    private void loadAvailableChallenges() {
        progressLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoAvailableChallenges.setVisibility(View.GONE);

        // Use getUserChallengeRecords() which is already implemented
        firebaseManager.getUserChallengeRecords()
                .addOnSuccessListener(userChallengeRecords -> {
                    // Extract Challenge objects from ChallengeRecord objects
                    List<Challenge> userChallenges = new ArrayList<>();
                    for (ChallengeRecord record : userChallengeRecords) {
                        if (record.getChallenge() != null) {
                            userChallenges.add(record.getChallenge());
                        }
                    }

                    // Get all available challenges from Challenge class
                    List<Challenge> allChallenges = Challenge.ALL_CHALLENGES;
                    if (allChallenges == null) {
                        allChallenges = new ArrayList<>();
                    }

                    if (!isAdded()) return;
                    progressLoading.setVisibility(View.GONE);

                    // Filter out challenges the user already has
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
                    tvNoAvailableChallenges.setText("Error loading challenges: " + e.getMessage());
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
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void setOnChallengeSelectedListener(OnChallengeSelectedListener listener) {
        this.listener = listener;
    }
}