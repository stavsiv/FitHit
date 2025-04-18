package com.example.fithit.Models;

import java.util.Date;
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

    public static boolean isValidType(String type) {
        return type != null && (
                type.equals(WEIGHT) ||
                        type.equals(HEART_RATE) ||
                        type.equals(STEPS) ||
                        type.equals(CALORIES)
        );
    }

    public void addMetric(String type, double value) {
        if (!isValidType(type)) {
            throw new IllegalArgumentException("Invalid metric type: " + type);
        }
        metrics.put(type, value);
    }

    public Double getMetricValue(String type) {
        return metrics.get(type);
    }

    public Map<String, Double> getAllMetrics() {
        return new HashMap<>(metrics);
    }


    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}