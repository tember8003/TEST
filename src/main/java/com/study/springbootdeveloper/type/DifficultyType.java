package com.study.springbootdeveloper.type;

public enum DifficultyType {
    BEGINNER("BEGINNER"),
    INTERMEDIATE("INTERMEDIATE"),
    ADVANCED("ADVANCED"),
;
    private final String description;

    DifficultyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
