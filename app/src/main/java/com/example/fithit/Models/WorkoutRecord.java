package com.example.fithit.Models;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkoutRecord {
    private long date;
    private Workout workout;
    private  Map<String, Double> metrics;
    private boolean isCompleted; // Added to track if workout was completed

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

    public void setMetric(String type, Double value) {
        if (Metric.isValidType(type)) {
            metrics.put(type, value);
        } else {
            throw new IllegalArgumentException("Invalid metric type: " + type);
        }
    }
/*
    public void setCompleted(boolean completed) {
        isCompleted = completed;
        if (completed && date == null) {
            date = new Date(); // Set completion time when workout is marked as done
        }
    }*/

    public void addMetric(String type, double value) {
        if (metrics == null) {
            metrics = new HashMap<>();
        }
        Log.d("WorkoutRecord", "Adding metric: " + type + " = " + value);
        metrics.put(type, value);
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }
}