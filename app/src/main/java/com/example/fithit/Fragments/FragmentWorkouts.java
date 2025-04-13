package com.example.fithit.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.fithit.Adapters.WorkoutsAdapter;
import com.example.fithit.Managers.FirebaseManager;
import com.example.fithit.Models.DatabaseWorkouts;
import com.example.fithit.Models.Workout;
import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentWorkouts extends Fragment {

    private Date selectedDate;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);

        if (getArguments() != null) {
            long dateInMillis = getArguments().getLong("selectedDate");
            selectedDate = new Date(dateInMillis);
        }

        RecyclerView workoutsRecyclerView = view.findViewById(R.id.workouts_recycler_view);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Workout> workoutsList = DatabaseWorkouts.getAllWorkouts();
        WorkoutsAdapter workoutsAdapter = new WorkoutsAdapter(workoutsList, this::openWorkoutDetails);
        workoutsRecyclerView.setAdapter(workoutsAdapter);

        return view;
    }

    private void openWorkoutDetails(Workout workout) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date normalizedDate = calendar.getTime();

        WorkoutRecord workoutRecord = new WorkoutRecord(workout, normalizedDate);

        workoutRecord.setCompleted(false);

        FirebaseManager.getInstance()
                .addWorkoutRecord(workoutRecord)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "The workout was successfully added to the calendar",Toast.LENGTH_SHORT).show();

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigateUp();

                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to save workout: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }
}
