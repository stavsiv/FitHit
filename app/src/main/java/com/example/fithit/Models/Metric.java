package com.example.fithit.Models;

import com.example.fithit.Enums.MetricType;
import java.util.Date;

public class Metric {
    private String metricId;
    private String userId;
    private String type;
    private double value;
    private long measurementDate;
    private String notes;

    public Metric() {}

    public Metric(String userId, MetricType metricType, double value, Date date, String notes) {
        this.userId = userId;
        this.type = metricType.name();
        this.value = value;
        this.measurementDate = date.getTime();
        this.notes = notes;
    }

    // Regular getters and setters
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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Helper methods for working with the MetricType enum
    public MetricType getMetricType() {
        return type != null ? MetricType.valueOf(type) : null;
    }

    public void setMetricType(MetricType metricType) {
        this.type = metricType != null ? metricType.name() : null;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(long measurementDate) {
        this.measurementDate = measurementDate;
    }

    // Helper methods for Date handling
    public Date getDate() {
        return new Date(measurementDate);
    }

    public void setDate(Date date) {
        this.measurementDate = date != null ? date.getTime() : 0;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}