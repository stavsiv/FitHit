package com.example.fithit.Models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkoutRecord {
    private Date workoutDateTime;
    private Workout workout;
    private Map<String, Double> metrics;

    public WorkoutRecord(Workout workout, Metric metric) { //check
    }

    public WorkoutRecord(Workout workout) {
        this.workoutDateTime = new Date();
        this.workout = workout;
        this.metrics = new HashMap<>();
    }

    // Getters and Setters
    public Date getWorkoutDateTime() {
        return workoutDateTime;
    }

    public void setWorkoutDateTime(Date workoutDateTime) {
        this.workoutDateTime = workoutDateTime;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Double getMetric(String type) {
        return metrics.get(type);
    }

    public void setMetric(String type, Double value) {
        if (Metric.isValidType(type)) {
            metrics.put(type, value);
        } else {
            throw new IllegalArgumentException("Invalid metric type: " + type);
        }
    }

    public void addMetric(String type, double value) {
        if (Metric.isValidType(type)) {
            metrics.put(type, value);
        } else {
            throw new IllegalArgumentException("Invalid metric type: " + type);
        }
    }
}