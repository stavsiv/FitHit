package com.example.fithit.Fragments;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fithit.R;
import com.google.android.material.card.MaterialCardView;


public class FragmentMain extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("FragmentMain", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // הגדרת המעבר לפרגמנט האזור האישי
        MaterialCardView btnToPersonalArea = view.findViewById(R.id.btn_to_personal_area);
        btnToPersonalArea.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_fragmentMain_to_fragmentPersonalArea)
        );

        return view;
    }

}
