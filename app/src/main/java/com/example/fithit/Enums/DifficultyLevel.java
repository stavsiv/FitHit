package com.example.fithit.Enums;
public enum DifficultyLevel {
    BEGINNER(1),
    INTERMEDIATE(2),
    EXPERT(3);

    private int value;

    DifficultyLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DifficultyLevel fromValue(int value) {
        switch (value) {
            case 1:
                return BEGINNER;
            case 2:
                return INTERMEDIATE;
            case 3:
                return EXPERT;
            default:
                return BEGINNER;
        }
    }
}