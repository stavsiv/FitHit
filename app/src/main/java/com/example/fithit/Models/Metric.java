package com.example.fithit.Models;

import java.util.HashMap;
import java.util.Map;

public class Metric {
    public static final String WEIGHT = "Weight";
    public static final String HEART_RATE = "Heart Rate";
    public static final String STEPS = "Steps";
    public static final String CALORIES = "Calories";
    private String metricId;
    private String userId;
    private Map<String, Double> metrics;

    public Metric() {
        this.userId = userId;
        this.metrics = new HashMap<>();
    }

}