package com.example.fithit.Models;

import com.example.fithit.Enums.DifficultyLevel;
//import com.example.fithit.Enums.WorkoutType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WorkoutBank {
    private Workout workoutId;
    private User userId;
    private List<Workout> workoutDatabase;
    private Map<DifficultyLevel, List<Workout>> workoutsByLevel;
    private Date completionDate;
    private int actualDuration;
    private String userNotes;
    private int difficultyRating;

    public WorkoutBank(Workout id, User userId, List<Workout> workoutDatabase,
                       Map<DifficultyLevel, List<Workout>> workoutsByLevel,
                       Date completionDate, int actualDuration,
                       String userNotes, int difficultyRating) {
        this.workoutId = id;
        this.userId = userId;
        this.workoutDatabase = workoutDatabase;
        this.workoutsByLevel = workoutsByLevel;
        this.completionDate = completionDate;
        this.actualDuration = actualDuration;
        this.userNotes = userNotes;
        this.difficultyRating = difficultyRating;
    }

    public List<Workout> getWorkoutsForUser(User user) {
        return workoutsByLevel.get(user.getCurrentDifficulty());
    }

    // המשך getters ו-setters מ-WorkoutHistory
}