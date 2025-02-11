package com.example.fithit.Models;

import com.example.fithit.Enums.MetricType;

import java.util.Date;

public class Metric {
    private int metricId;
    private int userId;
    private MetricType type;
    private double value;
    private Date measurementDate;
    private String notes;

    public Metric(int metricId, int userId, MetricType type, double value, Date measurementDate, String notes) {
        this.metricId = metricId;
        this.userId = userId;
        this.type = type;
        this.value = value;
        this.measurementDate = measurementDate;
        this.notes = notes;
    }

    public int getMetricId() {
        return metricId;
    }

    public void setMetricId(int metricId) {
        this.metricId = metricId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public MetricType getType() {
        return type;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(Date measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
