package com.study.springbootdeveloper.type;

public enum ProblemType {
    MULTIPLE_CHOICE("객관식"),
    SHORT_ANSWER("단답형"),
    DESCRIPTIVE("서술형")
    ;

    private final String description;

    ProblemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
