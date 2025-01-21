package com.example.fithit.Models;

import java.util.Date;

public class WorkoutHistory {
    private int id;
    private int userId;
    private Workout workout;
    private Date completionDate;
    private int actualDuration;
    //private List<ExerciseCompletion> exercisesCompleted;
    private String userNotes;
    private int difficultyRating;

    public WorkoutHistory(int id, int userId, Workout workout, Date completionDate, int actualDuration, String userNotes, int difficultyRating) {
        this.id = id;
        this.userId = userId;
        this.workout = workout;
        this.completionDate = completionDate;
        this.actualDuration = actualDuration;
        this.userNotes = userNotes;
        this.difficultyRating = difficultyRating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public int getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

    public int getDifficultyRating() {
        return difficultyRating;
    }

    public void setDifficultyRating(int difficultyRating) {
        this.difficultyRating = difficultyRating;
    }
}
