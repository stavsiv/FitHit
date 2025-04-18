package com.example.fithit.Models;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkoutRecord {
    private long date;
    private Workout workout;
    private  Map<String, Double> metrics;
    private boolean isCompleted;

    public WorkoutRecord() {
        this.metrics = new HashMap<>();
    }
    public WorkoutRecord(Workout workout, Date scheduledDate) {
        this.workout = workout;
        this.date = scheduledDate.getTime();
        this.metrics = new HashMap<>();
        this.isCompleted = false;
    }


    // Getters and Setters
    public long getDate() {  // Returns timestamp
        return date;
    }

    public void setDate(long date) {  // Takes timestamp
        this.date = date;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void addMetric(String type, double value) {
        if (metrics == null) {
            metrics = new HashMap<>();
        }
        metrics.put(type, value);
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }
}