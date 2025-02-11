package com.example.fithit.Models;

import com.example.fithit.Enums.MetricType;

import java.util.Date;
import java.util.Map;

public class UserGoals {
    //private int id;
    private int userId;
    private int weeklyWorkoutTarget;
    private Map<MetricType, Double> targetMetrics;
    private Date startDate;
    private Date endDate;
    //private GoalStatus status;
}
