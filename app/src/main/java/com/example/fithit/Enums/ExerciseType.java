package com.example.fithit.Enums;
public enum ExerciseType {
    STRENGTH("STRENGTH"),
    STRETCHING("STRETCHING"),
    BALANCE("BALANCE"),
    CARDIO("CARDIO");

    private final String ExerciseTypeName;

    ExerciseType(String exerciseTypeName) {
        this.ExerciseTypeName = exerciseTypeName;
    }

    public String getExerciseTypeName() {
        return ExerciseTypeName;
    }
}