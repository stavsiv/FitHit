package com.example.fithit.Fragments;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fithit.Adapters.ChallengeAdapter;
import com.example.fithit.Models.Challenge;
import com.example.fithit.Models.User;
import com.example.fithit.R;



import java.util.ArrayList;
import java.util.List;

public class ChallengesDialog extends DialogFragment {
    private RecyclerView recyclerView;
    private User currentUser;

    public ChallengesDialog(User user) {
        this.currentUser = user;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_challenges, container, false);

        // Setup close button
        ImageButton btnClose = view.findViewById(R.id.btnCloseDialog);
        btnClose.setOnClickListener(v -> dismiss());

        // Rest of the existing code...
        recyclerView = view.findViewById(R.id.recyclerViewChallenges);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Challenge> allChallenges = new ArrayList<>();
        allChallenges.addAll(Challenge.getChallengesByType("DAILY"));
        allChallenges.addAll(Challenge.getChallengesByType("WEEKLY"));

        ChallengeAdapter adapter = new ChallengeAdapter(allChallenges, currentUser);
        recyclerView.setAdapter(adapter);

        return view;
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
}