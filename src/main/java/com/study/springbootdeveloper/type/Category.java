package com.study.springbootdeveloper.type;

public enum Category {
    SPRING_CORE("Spring Core"),
    SPRING_BOOT("Spring Boot"),
    SPRING_MVC("Spring MVC"),
    SPRING_DATA_JPA("Spring Data JPA"),
    SPRING_SECURITY("Spring Security");
    ;

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}